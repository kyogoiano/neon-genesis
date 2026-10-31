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
package dev.neongenesis.cdi.hibernate;

import jakarta.enterprise.context.Dependent;
import jakarta.ejb.SessionContext;

import dev.neongenesis.cdi.CDITransactionContext;
import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.command.hibernate.HibernateCommand;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

@Dependent
public class CDIHibernateTransactionalInjector implements TransactionalInjector {
   private static volatile SessionFactory sharedSessionFactory;

   private SessionContext ejbSessionContext;
   private boolean transactional;
   private HibernateCommand hibernateCommand;
   private Session session;
   private Transaction transaction;

   public static synchronized SessionFactory getSharedSessionFactory()
         throws HibernateException {
      if (sharedSessionFactory == null) {
         String cfgResource = System.getProperty("genesis.hibernate.cfg");
         Configuration cfg = new Configuration();
         if (cfgResource != null && !cfgResource.trim().isEmpty()) {
            cfg.configure(cfgResource);
         } else {
            cfg.configure();
         }
         sharedSessionFactory = cfg.buildSessionFactory();
      }
      return sharedSessionFactory;
   }

   @Override
   public void init(Object context) {
      if (context instanceof SessionContext) {
         this.ejbSessionContext = (SessionContext)context;
      }
      if (Boolean.getBoolean("genesis.cdi.preLoadSessionFactory")
            || Boolean.getBoolean("genesis.aspect.preLoadSessionFactory")) {
         getSharedSessionFactory();
      }
   }

   @Override
   public void beforeInvocation(final Object target, final boolean transactional)
         throws HibernateException {
      this.transactional = transactional;

      if (!(target instanceof HibernateCommand)) {
         this.hibernateCommand = null;
         return;
      }

      this.hibernateCommand = (HibernateCommand)target;
      this.session = getSharedSessionFactory().openSession();
      this.transaction = null;
      this.hibernateCommand.setSession(this.session);

      if (transactional) {
         this.transaction = this.session.beginTransaction();
         this.session.setHibernateFlushMode(FlushMode.COMMIT);
      }
   }

   @Override
   public void afterInvocation() throws HibernateException {
      if (this.session != null && this.transactional) {
         this.session.flush();
      }
      if (this.transaction != null) {
         this.transaction.commit();
      }
   }

   @Override
   public void onException(final Exception e) {
      if (this.transactional) {
         try {
            CDITransactionContext.getInstance().setRollbackOnly();
         } catch (Exception ignored) {
         }
         if (this.ejbSessionContext != null) {
            try {
               this.ejbSessionContext.setRollbackOnly();
            } catch (Exception ignored) {
            }
         }
      }
      if (this.transaction != null) {
         try {
            this.transaction.rollback();
         } catch (final Exception ex) {
            throw new RuntimeException(ex);
         }
      }
   }

   @Override
   public void onFinally() throws HibernateException {
      if (this.hibernateCommand != null) {
         this.hibernateCommand.setSession(null);
         this.hibernateCommand = null;
      }

      this.transaction = null;

      if (this.session != null) {
         try {
            this.session.close();
         } finally {
            this.session = null;
         }
      }
   }
}
