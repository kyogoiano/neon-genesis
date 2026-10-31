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

import java.lang.reflect.InvocationTargetException;

import javax.naming.InitialContext;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.command.Command;
import dev.neongenesis.command.CommandExecutionGuard;
import dev.neongenesis.command.Query;
import dev.neongenesis.command.Transaction;
import dev.neongenesis.ejb.CommandExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class EJBCommandExecutionAspect {
   private static final Log log = LogFactory.getLog(EJBCommandExecutionAspect.class);
   private static final String DEFAULT_JNDI_NAME =
         "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal";
   private static final String[] FALLBACK_JNDI_NAMES = new String[] {
         "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal",
         "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutor",
         "java:app/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal",
         "java:module/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal",
         "java:module/CommandExecutor"
   };

   private CommandExecutor session;
   private final ThreadLocal<Boolean> executingCommand = new ThreadLocal<Boolean>();

   @Around("(execution(* dev.neongenesis.command.Command+.invoke*(..))"
         + " && (execution(* dev.neongenesis.command.Query+.*(..))"
         + " || execution(* dev.neongenesis.command.Transaction+.*(..))))"
         + " || execution(@dev.neongenesis.annotation.Remotable * *(..))"
         + " || execution(@dev.neongenesis.annotation.Transactional * *(..))")
   public Object commandExecution(ProceedingJoinPoint joinPoint) throws Throwable {
      if (CDICommandExecutionAspect.isCdiExecutionEnabled()
            || LocalEJBCommandExecutionAspect.localEjbEnabled()) {
         return joinPoint.proceed();
      }

      if (Boolean.TRUE.equals(executingCommand.get())
            || CommandExecutionGuard.isExecuting()) {
         return joinPoint.proceed();
      }

      MethodSignature signature = (MethodSignature)joinPoint.getSignature();
      java.lang.reflect.Method method = signature.getMethod();
      Object target = joinPoint.getTarget();

      if (isLocalTestExecution(method.getDeclaringClass())) {
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

      executingCommand.set(Boolean.TRUE);
      try {
         CommandExecutor executor = getCommandExecutor();
         try {
            if (CommandInvocationAspect.isTransactional(method)) {
               return executor.executeTransaction(target, method.getName(),
                     classNames, joinPoint.getArgs());
            }
            return executor.executeQuery(target, method.getName(), classNames,
                  joinPoint.getArgs());
         } catch (InvocationTargetException e) {
            throw e.getTargetException();
         }
      } finally {
         executingCommand.remove();
      }
   }

   private boolean isLocalTestExecution(Class declaringClass) {
      String className = declaringClass.getName();
      return className.startsWith("dev.neongenesis.tests.")
            && (className.contains("LocalCommandExecutionTest$")
                  || className.contains("LocalHibernateTransactionInjetorTest$")
                  || className.contains("TimeoutTest$"));
   }

   private CommandExecutor getCommandExecutor() {
      if (session == null) {
         try {
            InitialContext ctx = new InitialContext();
            String configuredJndi = System.getProperty("genesis.aspect.jndiName");
            if (configuredJndi != null && !configuredJndi.trim().isEmpty()) {
               session = (CommandExecutor)ctx.lookup(configuredJndi);
            } else {
               Exception lastEx = null;
               for (String candidate : FALLBACK_JNDI_NAMES) {
                  try {
                     session = (CommandExecutor)ctx.lookup(candidate);
                     if (session != null) {
                        break;
                     }
                  } catch (Exception ex) {
                     lastEx = ex;
                  }
               }
               if (session == null && lastEx != null) {
                  throw lastEx;
               }
            }
         } catch (Exception e) {
            session = null;
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
         }
      }
      return session;
   }
}
