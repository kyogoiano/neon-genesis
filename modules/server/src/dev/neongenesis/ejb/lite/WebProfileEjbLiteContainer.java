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
package dev.neongenesis.ejb.lite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.Principal;
import java.util.Map;

import jakarta.annotation.Resource;
import jakarta.ejb.EJBHome;
import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.EJBLocalObject;
import jakarta.ejb.EJBObject;
import jakarta.ejb.SessionContext;
import jakarta.ejb.TimerService;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import dev.neongenesis.cdi.CDITransactionContext;
import dev.neongenesis.cdi.jndi.CDIInitialContextFactory;
import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.ejb.CommandExecutor;
import dev.neongenesis.ejb.CommandExecutorEJB;
import dev.neongenesis.ejb.CommandExecutorLocal;

public class WebProfileEjbLiteContainer implements AutoCloseable {
   private final CommandExecutorLocal proxy;

   public WebProfileEjbLiteContainer() {
      String configuredInjector = System.getProperty(
            "genesis.ejb.transactionalInjector");
      if (configuredInjector != null && !configuredInjector.trim().isEmpty()) {
         CDIInitialContextFactory.bindStatic(
               "java:comp/env/TransactionalInjector", configuredInjector);
      }

      this.proxy = (CommandExecutorLocal)Proxy.newProxyInstance(
            CommandExecutorLocal.class.getClassLoader(),
            new Class<?>[] { CommandExecutorLocal.class, CommandExecutor.class },
            new EjbLiteInvocationHandler());

      CDIInitialContextFactory.bindStatic(
            "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal",
            this.proxy);
      CDIInitialContextFactory.bindStatic(
            "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutor",
            this.proxy);
      CDIInitialContextFactory.bindStatic(
            "java:app/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal",
            this.proxy);
      CDIInitialContextFactory.bindStatic(
            "java:module/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal",
            this.proxy);
      CDIInitialContextFactory.bindStatic(
            "java:module/CommandExecutor",
            this.proxy);
   }

   public CommandExecutorLocal getCommandExecutorLocal() {
      return proxy;
   }

   public CommandExecutorEJB newManagedEjbInstance() {
      try {
         CommandExecutorEJB bean = new CommandExecutorEJB();
         SessionContext sessionContext = new EjbLiteSessionContext();
         Instance<TransactionalInjector> cdiInjector = null;
         try {
            cdiInjector = CDI.current().select(TransactionalInjector.class);
         } catch (Throwable ignored) {
         }

         for (Field field : CommandExecutorEJB.class.getDeclaredFields()) {
            if (field.isAnnotationPresent(Resource.class)
                  && SessionContext.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               field.set(bean, sessionContext);
            } else if (field.isAnnotationPresent(Inject.class)
                  && Instance.class.isAssignableFrom(field.getType())) {
               field.setAccessible(true);
               field.set(bean, cdiInjector);
            }
         }

         bean.initialize();
         return bean;
      } catch (Exception e) {
         throw new RuntimeException("Failed to initialize EJB Lite CommandExecutorEJB", e);
      }
   }

   @Override
   public void close() {
   }

   private final class EjbLiteInvocationHandler implements InvocationHandler {
      @Override
      public Object invoke(Object proxyObj, Method method, Object[] args)
            throws Throwable {
         if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
         }

         CommandExecutorEJB bean = newManagedEjbInstance();
         Method beanMethod = CommandExecutorEJB.class.getMethod(
               method.getName(), method.getParameterTypes());
         TransactionAttribute txAttr = beanMethod.getAnnotation(
               TransactionAttribute.class);
         boolean requiredTx = txAttr != null
               && txAttr.value() == TransactionAttributeType.REQUIRED;

         if (requiredTx) {
            CDITransactionContext.enterTransaction();
         } else {
            CDITransactionContext.enterNonTransactional();
         }

         Object[] effectiveArgs = args;
         if (args != null && args.length > 0 && args[0] instanceof Serializable
               && !Boolean.getBoolean("genesis.ejb.passByReference")) {
            effectiveArgs = args.clone();
            effectiveArgs[0] = cloneSerializable(args[0]);
         }

         try {
            return beanMethod.invoke(bean, effectiveArgs);
         } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
         } finally {
            CDITransactionContext.clear();
         }
      }

      private Object cloneSerializable(Object obj) {
         try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
               oos.writeObject(obj);
            }
            try (ObjectInputStream ois = new ObjectInputStream(
                  new ByteArrayInputStream(baos.toByteArray()))) {
               ois.setObjectInputFilter(info -> ObjectInputFilter.Status.ALLOWED);
               return ois.readObject();
            }
         } catch (Exception e) {
            return obj;
         }
      }
   }

   private static final class EjbLiteSessionContext implements SessionContext {
      @Override
      public EJBLocalObject getEJBLocalObject() throws IllegalStateException {
         return null;
      }

      @Override
      public EJBObject getEJBObject() throws IllegalStateException {
         return null;
      }

      @Override
      public <T> T getBusinessObject(Class<T> businessInterface)
            throws IllegalStateException {
         return null;
      }

      @Override
      public Class getInvokedBusinessInterface() throws IllegalStateException {
         return CommandExecutorLocal.class;
      }

      @Override
      public boolean wasCancelCalled() throws IllegalStateException {
         return false;
      }

      @Override
      public EJBHome getEJBHome() throws IllegalStateException {
         return null;
      }

      @Override
      public EJBLocalHome getEJBLocalHome() throws IllegalStateException {
         return null;
      }

      @Override
      public Principal getCallerPrincipal() throws IllegalStateException {
         return null;
      }

      @Override
      public boolean isCallerInRole(String roleName)
            throws IllegalStateException {
         return false;
      }

      @Override
      public UserTransaction getUserTransaction() throws IllegalStateException {
         return CDITransactionContext.getInstance();
      }

      @Override
      public void setRollbackOnly() throws IllegalStateException {
         try {
            CDITransactionContext.getInstance().setRollbackOnly();
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
      }

      @Override
      public boolean getRollbackOnly() throws IllegalStateException {
         try {
            return CDITransactionContext.getInstance().getStatus()
                  == Status.STATUS_MARKED_ROLLBACK;
         } catch (Exception e) {
            return false;
         }
      }

      @Override
      public TimerService getTimerService() throws IllegalStateException {
         return null;
      }

      @Override
      public Object lookup(String name) throws IllegalArgumentException {
         try {
            return new javax.naming.InitialContext().lookup(name);
         } catch (Exception e) {
            throw new IllegalArgumentException(e);
         }
      }

      @Override
      public Map<String, Object> getContextData() {
         return java.util.Collections.emptyMap();
      }
   }
}
