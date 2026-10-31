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
package dev.neongenesis.cdi;

import java.lang.reflect.InvocationTargetException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dev.neongenesis.cdi.hibernate.CDIHibernateTransactionalInjector;
import dev.neongenesis.command.CommandExecutionGuard;
import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.ejb.CommandExecutor;
import dev.neongenesis.reflection.ReflectionInvoker;

@ApplicationScoped
public class CommandExecutorCDI implements CommandExecutor {
   @Inject
   private Instance<TransactionalInjector> cdiInjector;

   private String customInjectorClassName;

   @PostConstruct
   public void initialize() {
      String className = System.getProperty("genesis.cdi.transactionalInjector");
      if (className == null || className.trim().isEmpty()) {
         try {
            className = (String)new InitialContext().lookup(
                  "java:comp/env/TransactionalInjector"); // NOI18N
         } catch (NamingException ignored) {
         }
      }
      this.customInjectorClassName = className;
   }

   public TransactionalInjector createTransactionalInjector() {
      TransactionalInjector injector = null;
      try {
         if (customInjectorClassName != null
               && !customInjectorClassName.trim().isEmpty()) {
            injector = (TransactionalInjector)Class.forName(
                  customInjectorClassName, true,
                  Thread.currentThread().getContextClassLoader())
                  .getDeclaredConstructor().newInstance();
         } else if (cdiInjector != null && !cdiInjector.isUnsatisfied()) {
            injector = cdiInjector.get();
         } else {
            injector = new CDIHibernateTransactionalInjector();
         }
      } catch (final Exception e) {
         throw new RuntimeException(e);
      }

      injector.init(null);
      return injector;
   }

   @Override
   public Object executeTransaction(Object o, String methodName,
         String[] classNames, Object[] args) throws ClassNotFoundException,
         IllegalAccessException, NoSuchMethodException,
         InvocationTargetException {
      return execute(o, methodName, classNames, args, true);
   }

   @Override
   public Object executeQuery(Object o, String methodName,
         String[] classNames, Object[] args) throws ClassNotFoundException,
         IllegalAccessException, NoSuchMethodException,
         InvocationTargetException {
      return execute(o, methodName, classNames, args, false);
   }

   private Object execute(Object o, String methodName, String[] classNames,
         Object[] args, boolean transactional) throws ClassNotFoundException,
         IllegalAccessException, NoSuchMethodException,
         InvocationTargetException {
      final TransactionalInjector injector = createTransactionalInjector();
      final boolean fromCdi = (customInjectorClassName == null
            || customInjectorClassName.trim().isEmpty())
            && cdiInjector != null && !cdiInjector.isUnsatisfied();
      Exception e = null;

      if (transactional) {
         CDITransactionContext.enterTransaction();
      } else {
         CDITransactionContext.enterNonTransactional();
      }

      try {
         injector.beforeInvocation(o, transactional);

         final Object ret;
         CommandExecutionGuard.enter();
         try {
            ret = ReflectionInvoker.getInstance().invoke(o, methodName,
                  classNames, args);
         } finally {
            CommandExecutionGuard.exit();
         }

         injector.afterInvocation();

         return ret;
      } catch (final ClassNotFoundException cnfe) {
         e = cnfe;
         throw cnfe;
      } catch (final IllegalAccessException iae) {
         e = iae;
         throw iae;
      } catch (final NoSuchMethodException nsme) {
         e = nsme;
         throw nsme;
      } catch (final InvocationTargetException ite) {
         e = ite;
         throw ite;
      } catch (final Exception ex) {
         e = ex;
         throw new InvocationTargetException(ex);
      } finally {
         if (e != null) {
            injector.onException(e);
         }

         try {
            injector.onFinally();
         } catch (final Exception ex) {
            throw new InvocationTargetException(combine(ex, e));
         } finally {
            CDITransactionContext.clear();
            if (fromCdi) {
               try {
                  cdiInjector.destroy(injector);
               } catch (Exception ignored) {
               }
            }
         }
      }
   }

   private Throwable combine(final Throwable t, final Exception e) {
      Throwable last = t;

      while (last.getCause() != null) {
         last = last.getCause();
      }

      last.initCause(e);

      return t;
   }
}
