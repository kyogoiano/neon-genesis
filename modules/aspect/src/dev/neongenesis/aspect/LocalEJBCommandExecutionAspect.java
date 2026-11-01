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

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import javax.naming.InitialContext;

import dev.neongenesis.ejb.CommandExecutorLocal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class LocalEJBCommandExecutionAspect {
   private static final Log log =
         LogFactory.getLog(LocalEJBCommandExecutionAspect.class);

   private final ThreadLocal<Method> commandMethod = new ThreadLocal<Method>();
   private CommandExecutorLocal session;

   @Pointcut("(execution(@dev.neongenesis.annotation.Remotable * *(..))"
         + " || execution(@dev.neongenesis.annotation.Transactional * *(..)))")
   public void commandExecution() {
   }

   @Around("commandExecution()")
   public Object commandExecutionAdvice(ProceedingJoinPoint joinPoint)
         throws Throwable {
      if (!localEjbEnabled()) {
         return joinPoint.proceed();
      }

      Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
      if (method.equals(commandMethod.get())) {
         return joinPoint.proceed();
      }

      if (!CommandInvocationAspect.isTransactional(method)
            && !CommandInvocationAspect.isRemotable(method)) {
         return joinPoint.proceed();
      }

      Class[] parameterTypes = method.getParameterTypes();
      String[] classNames = new String[parameterTypes.length];
      for (int i = 0; i < parameterTypes.length; i++) {
         classNames[i] = parameterTypes[i].getName();
      }

      try {
         CommandExecutorLocal executor = getCommandExecutor();
         commandMethod.set(method);
         if (CommandInvocationAspect.isTransactional(method)) {
            return executor.executeTransaction(joinPoint.getTarget(),
                  method.getName(), classNames, joinPoint.getArgs());
         }
         return executor.executeQuery(joinPoint.getTarget(), method.getName(),
               classNames, joinPoint.getArgs());
      } catch (InvocationTargetException e) {
         throw e.getTargetException();
      } finally {
         commandMethod.remove();
      }
   }

   public static boolean localEjbEnabled() {
      return Boolean.getBoolean("genesis.aspect.localEjb");
   }

   private CommandExecutorLocal getCommandExecutor() {
      if (session == null) {
         try {
            String jndiName = System.getProperty(
                  "genesis.aspect.localEjbJndiName");
            if (jndiName == null) {
               throw new IllegalStateException(
                     "Set genesis.aspect.localEjbJndiName before enabling local EJB execution");
            }
            session = (CommandExecutorLocal)new InitialContext()
                  .lookup(jndiName);
         } catch (Exception e) {
            session = null;
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
         }
      }
      return session;
   }
}
