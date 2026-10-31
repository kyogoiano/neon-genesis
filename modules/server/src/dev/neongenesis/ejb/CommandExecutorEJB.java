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
package dev.neongenesis.ejb;

import java.lang.reflect.InvocationTargetException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.ejb.EJBException;
import jakarta.ejb.Local;
import jakarta.ejb.SessionContext;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.command.CommandExecutionGuard;
import dev.neongenesis.reflection.ReflectionInvoker;

@Stateless(name = "CommandExecutor")
@Local(CommandExecutorLocal.class)
public class CommandExecutorEJB implements CommandExecutorLocal, CommandExecutor {
   private TransactionalInjector injector;
   @Resource
   private SessionContext sessionContext;
   @Inject
   private Instance<TransactionalInjector> cdiInjector;
   
   @PostConstruct
   public void initialize() {
      try {
         String className = null;
         try {
            final InitialContext ctx = new InitialContext();
            className = (String)ctx.lookup(
                  "java:comp/env/TransactionalInjector"); // NOI18N
         } catch (final NamingException ne) {
            className = System.getProperty("genesis.ejb.transactionalInjector");
         }

         if (className != null && !className.trim().isEmpty()) {
            injector = (TransactionalInjector)Class.forName(className, true, 
                  Thread.currentThread().getContextClassLoader())
                  .getDeclaredConstructor().newInstance();
         } else if (cdiInjector != null && !cdiInjector.isUnsatisfied()) {
            injector = cdiInjector.get();
         } else {
            throw new IllegalStateException(
                  "No TransactionalInjector configured in JNDI (java:comp/env/TransactionalInjector) or CDI");
         }
      } catch (final Exception e) {
         throw new EJBException(e);
      }

      injector.init(sessionContext);
   }

   public TransactionalInjector getTransactionalInjector() {
      return injector;
   }

   @Override
   @TransactionAttribute(TransactionAttributeType.REQUIRED)
   public Object executeTransaction(Object o, String methodName,
         String[] classNames, Object[] args) throws ClassNotFoundException,
                              IllegalAccessException, NoSuchMethodException,
                              InvocationTargetException {
       return execute(o, methodName, classNames, args, true);
   }

   @Override
   @TransactionAttribute(TransactionAttributeType.SUPPORTS)
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
      Exception e = null;

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