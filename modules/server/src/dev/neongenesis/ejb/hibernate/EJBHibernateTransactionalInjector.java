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

import jakarta.ejb.EJBException;
import jakarta.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dev.neongenesis.command.hibernate.HibernateCommand;
import dev.neongenesis.command.TransactionalInjector;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class EJBHibernateTransactionalInjector implements TransactionalInjector {
   private SessionContext ctx;
   private SessionFactory sessionFactory;
   private boolean transactional;
   private Session session;

   public void init(Object context) {
      this.ctx = (SessionContext)context;

      try {
         final InitialContext ctx = new InitialContext();

         sessionFactory = (SessionFactory) ctx.lookup((String) ctx
               .lookup("java:comp/env/HibernateFactoryAddress")); // NOI18N
      } catch (NamingException ne) {
         throw new EJBException(ne);
      }
   }

   public void beforeInvocation(Object target, boolean transactional) 
                                                   throws HibernateException {
      this.transactional = transactional;

      if (!(target instanceof HibernateCommand)) {
         return;
      }
      
      session = sessionFactory.openSession();
      ((HibernateCommand)target).setSession(session);
   }

   public void afterInvocation() throws HibernateException {
      if (session == null || !transactional) {
         return;
      }

      session.flush();
   }
   
   public void onException(Exception e) {
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