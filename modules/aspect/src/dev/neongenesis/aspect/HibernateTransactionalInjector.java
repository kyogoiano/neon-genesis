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

import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.command.hibernate.HibernateCommand;
import dev.neongenesis.util.Bundle;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HibernateTransactionalInjector implements TransactionalInjector {
   private static final Log log =
         LogFactory.getLog(HibernateTransactionalInjector.class);
   private boolean isHibernateCommand;
   private Transaction transaction;
   private HibernateCommand hibernateCommand;
   private Session session;
   
   public void init(Object context) {
      if (!Boolean.getBoolean("genesis.aspect.preLoadSessionFactory")) {
         return;
      }
      
      try {
         HibernateHelper.getInstance().getSessionFactory();
      } catch (HibernateException he) {
         log.error(Bundle.getMessage(HibernateTransactionalInjector.class,
               "ERROR_PRELOADING_SESSIONFACTORY"), he); // NOI18N
      }
   }
   
   public void beforeInvocation(final Object target, final boolean transactional)
         throws HibernateException {
      isHibernateCommand = target instanceof HibernateCommand;
      
      if (!isHibernateCommand) {
         return;
      }
      
      hibernateCommand = (HibernateCommand)target;
      session = HibernateHelper.getInstance().createSession();
      
      transaction = null;
      
      hibernateCommand.setSession(session);
      
      if (transactional) {
         transaction = session.beginTransaction();
         session.setHibernateFlushMode(FlushMode.COMMIT);
      }
   }
   
   public void afterInvocation() throws HibernateException {
      if (transaction != null) {
         transaction.commit();
      }
   }
   
   public void onException(final Exception e) {
      if (transaction != null) {
         try {
            transaction.rollback();
         } catch (final Exception ex) {
            throw new RuntimeException(ex);
         }
      }
   }
   
   public void onFinally() throws HibernateException {
      if(hibernateCommand != null){
         hibernateCommand.setSession(null);
         hibernateCommand = null;
      }
      
      transaction = null;
      
      if(session != null){
         try {
            session.close();
         } finally {
            session = null;
         }
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
         factory = new Configuration().configure().buildSessionFactory();
      }
      
      return factory;
   }
   
   public Session createSession() throws HibernateException {
      return getSessionFactory().openSession();
   }
}