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
package dev.neongenesis.ejb.hibernate;

import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.command.hibernate.HibernateCommand;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import jakarta.ejb.EJBException;
import jakarta.ejb.SessionContext;


public class EJBHibernateCfgTransactionalInjector
   implements TransactionalInjector {
   private SessionContext ctx;
   private SessionFactory sessionFactory;
   private boolean transactional;
   private Session session;

   public void init(Object context) {
      this.ctx = (SessionContext) context;

      try {
         sessionFactory = HibernateHelper.getInstance().getSessionFactory();
      } catch (HibernateException he) {
         throw new EJBException(he);
      }
   }

   public void beforeInvocation(final Object target, final boolean transactional)
      throws HibernateException {
      this.transactional = transactional;

      if (!(target instanceof HibernateCommand)) {
         return;
      }

      session = sessionFactory.openSession();
      ((HibernateCommand) target).setSession(session);
   }

   public void afterInvocation() throws HibernateException {
      if ((session == null) || !transactional) {
         return;
      }

      session.flush();
   }

   public void onException(final Exception e) {
      if (transactional) {
         ctx.setRollbackOnly();
      }
   }

   public void onFinally() throws HibernateException {
      if (session == null) {
         return;
      }

      try {
         session.close();
      } finally {
         session = null;
      }
   }
}


final class HibernateHelper {
   private static final HibernateHelper instance = new HibernateHelper();
   private SessionFactory factory;

   private HibernateHelper() {
   }

   public static HibernateHelper getInstance() {
      return instance;
   }

   public SessionFactory getSessionFactory() throws HibernateException {
      if (factory == null) {
         factory = new Configuration().configure("/remote-hibernate.cfg.xml"). // NOI18N
               buildSessionFactory();
      }

      return factory;
   }

   public Session createSession() throws HibernateException {
      return getSessionFactory().openSession();
   }
}
