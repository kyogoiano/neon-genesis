/*
 * Neon Genesis
 * Copyright (C) 2026 Leandro Cardoso de Andrade (@kyogoiano <leandro.ueg@gmail.com>)
 * Original Genesis Project Copyright (C) 2004-2009 Summa Technologies do Brasil Ltda.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.neongenesis.aspect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jakarta.enterprise.inject.spi.CDI;

import dev.neongenesis.cdi.CommandExecutorCDI;
import dev.neongenesis.cdi.helidon.HelidonCommandClient;
import dev.neongenesis.command.CommandExecutionGuard;
import dev.neongenesis.ejb.CommandExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class CDICommandExecutionAspect {
   private static final Log log = LogFactory.getLog(CDICommandExecutionAspect.class);

   private volatile String cachedNimaUrl;
   private volatile HelidonCommandClient nimaClient;
   private final ThreadLocal<Boolean> executingCommand = new ThreadLocal<Boolean>();

   public static boolean isCdiExecutionEnabled() {
      if (LocalEJBCommandExecutionAspect.localEjbEnabled()) {
         return false;
      }
      String mode = System.getProperty("genesis.aspect.executionMode");
      if ("ejb".equalsIgnoreCase(mode)) {
         return false;
      }
      if ("cdi".equalsIgnoreCase(mode) || "nima".equalsIgnoreCase(mode)
            || Boolean.getBoolean("genesis.aspect.cdi")) {
         return true;
      }
      String nimaUrl = System.getProperty("genesis.aspect.nima.url");
      if (nimaUrl != null && !nimaUrl.trim().isEmpty()) {
         return true;
      }
      try {
         CDI.current();
         return true;
      } catch (Throwable t) {
         return false;
      }
   }

   @Around("(execution(@dev.neongenesis.annotation.Remotable * *(..))"
         + " || execution(@dev.neongenesis.annotation.Transactional * *(..)))")
   public Object commandExecution(ProceedingJoinPoint joinPoint) throws Throwable {
      if (!isCdiExecutionEnabled()) {
         return joinPoint.proceed();
      }

      if (Boolean.TRUE.equals(executingCommand.get())
            || CommandExecutionGuard.isExecuting()) {
         return joinPoint.proceed();
      }

      MethodSignature signature = (MethodSignature)joinPoint.getSignature();
      Method method = signature.getMethod();
      Object target = joinPoint.getTarget();

      if (isLocalTestExecution(method.getDeclaringClass())) {
         return joinPoint.proceed();
      }

      if (!CommandInvocationAspect.isTransactional(method)
            && !CommandInvocationAspect.isRemotable(method)) {
         return joinPoint.proceed();
      }

      Class<?>[] parameterTypes = method.getParameterTypes();
      String[] classNames = new String[parameterTypes.length];
      for (int i = 0; i < parameterTypes.length; i++) {
         classNames[i] = parameterTypes[i].getName();
      }

      executingCommand.set(Boolean.TRUE);
      try {
         boolean useRemoteNima = shouldUseRemoteNima();
         CommandExecutor executor = getCommandExecutor(useRemoteNima);
         Object invocationTarget = useRemoteNima
               ? target
               : copyIfSerializable(target);
         try {
            if (CommandInvocationAspect.isTransactional(method)) {
               return executor.executeTransaction(invocationTarget,
                     method.getName(), classNames, joinPoint.getArgs());
            }
            return executor.executeQuery(invocationTarget, method.getName(),
                  classNames, joinPoint.getArgs());
         } catch (InvocationTargetException e) {
            throw e.getTargetException();
         }
      } finally {
         executingCommand.remove();
      }
   }

   private boolean shouldUseRemoteNima() {
      String transport = System.getProperty("genesis.aspect.cdiTransport", "auto");
      if ("local".equalsIgnoreCase(transport)
            || "inprocess".equalsIgnoreCase(transport)) {
         return false;
      }
      String nimaUrl = System.getProperty("genesis.aspect.nima.url");
      return nimaUrl != null && !nimaUrl.trim().isEmpty();
   }

   private CommandExecutor getCommandExecutor(boolean useRemoteNima) {
      if (useRemoteNima) {
         String nimaUrl = System.getProperty("genesis.aspect.nima.url");
         if (nimaClient == null || !nimaUrl.equals(cachedNimaUrl)) {
            synchronized (this) {
               if (nimaClient == null || !nimaUrl.equals(cachedNimaUrl)) {
                  cachedNimaUrl = nimaUrl;
                  nimaClient = new HelidonCommandClient(nimaUrl);
               }
            }
         }
         return nimaClient;
      }

      try {
         return CDI.current().select(CommandExecutorCDI.class).get();
      } catch (Exception e) {
         log.error(e.getMessage(), e);
         throw new RuntimeException(e);
      }
   }

   private Object copyIfSerializable(Object target) {
      if (Boolean.getBoolean("genesis.aspect.cdiPassByReference")
            || !(target instanceof Serializable)) {
         return target;
      }
      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(target);
         }
         try (ObjectInputStream ois = new ObjectInputStream(
               new ByteArrayInputStream(baos.toByteArray()))) {
            ois.setObjectInputFilter(info -> ObjectInputFilter.Status.ALLOWED);
            return ois.readObject();
         }
      } catch (Exception e) {
         return target;
      }
   }

   private boolean isLocalTestExecution(Class<?> declaringClass) {
      String className = declaringClass.getName();
      return className.startsWith("dev.neongenesis.tests.")
            && (className.contains("LocalCommandExecutionTest$")
                  || className.contains("LocalHibernateTransactionInjetorTest$")
                  || className.contains("TimeoutTest$"));
   }
}
