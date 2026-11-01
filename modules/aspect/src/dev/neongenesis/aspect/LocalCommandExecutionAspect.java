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

import dev.neongenesis.command.TransactionalInjector;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class LocalCommandExecutionAspect {
   private final ThreadLocal<TransactionalInjector> injectorPerThread =
         new ThreadLocal<TransactionalInjector>();
   private final ThreadLocal<Boolean> processingCommand =
         new ThreadLocal<Boolean>();

   @Around("(execution(@dev.neongenesis.annotation.Remotable * *(..))"
         + " || execution(@dev.neongenesis.annotation.Transactional * *(..)))")
   public Object commandExecution(ProceedingJoinPoint joinPoint) throws Throwable {
      Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
      if (!isLocalTestExecution(method.getDeclaringClass())) {
         return joinPoint.proceed();
      }

      Object target = joinPoint.getTarget();
      boolean transactional = CommandInvocationAspect.isTransactional(method);
      if ((!transactional && !CommandInvocationAspect.isRemotable(method))
            || Boolean.TRUE.equals(processingCommand.get())) {
         return joinPoint.proceed();
      }

      processingCommand.set(Boolean.TRUE);
      try {
         TransactionalInjector injector = getTransactionalInjector();
         injector.beforeInvocation(target, transactional);
         try {
            Object result = joinPoint.proceed();
            injector.afterInvocation();
            return result;
         } catch (Exception e) {
            injector.onException(e);
            throw e;
         } finally {
            injector.onFinally();
         }
      } finally {
         processingCommand.remove();
      }
   }

   private boolean isLocalTestExecution(Class declaringClass) {
      String className = declaringClass.getName();
      return className.startsWith("dev.neongenesis.tests.")
            && (className.contains("LocalCommandExecutionTest$")
                  || className.contains("LocalHibernateTransactionInjetorTest$"));
   }

   private TransactionalInjector getTransactionalInjector() throws Exception {
      TransactionalInjector injector = injectorPerThread.get();
      if (injector == null) {
         Thread currentThread = Thread.currentThread();
         String className = System.getProperty(
               "genesis.aspect.transactionalInjector",
               "dev.neongenesis.aspect.HibernateTransactionalInjector");
         ClassLoader loader = currentThread.getContextClassLoader();
         injector = (TransactionalInjector)Class.forName(className, true, loader)
               .getDeclaredConstructor().newInstance();
         injector.init(null);
         injectorPerThread.set(injector);
      }
      return injector;
   }
}
