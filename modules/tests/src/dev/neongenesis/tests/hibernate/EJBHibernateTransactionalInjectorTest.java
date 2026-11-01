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
package dev.neongenesis.tests.hibernate;

import java.util.List;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.command.hibernate.AbstractHibernateCommand;
import dev.neongenesis.tests.TestCase;
import org.hibernate.Hibernate;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.query.Query;

public class EJBHibernateTransactionalInjectorTest extends TestCase {
   public void tearDown() throws Exception {
      new HibernateCommand().deleteAll();
   }

   public void testSuccessfullyQuery() throws Exception {
      final HibernateCommand command = new HibernateCommand();
      command.insertBean("first bean");
      command.insertBean("seconde bean");
      final List list = command.getBeans();
      assertNotNull(list);
      assertEquals(2, list.size());
   }

   public void testUnsuccessfullyQuery() throws Exception {
      Exception ex = null;
      try {
         new HibernateCommand().getNonExistentBean(HibernateBean.class);
      } catch (Exception e) {
         ex = e;
      }
      assertNotNull(ex);
      assertTrue(ex instanceof ObjectNotFoundException);
   }

   public void testSuccessfullyTransaction() throws Exception {
      final HibernateCommand command = new HibernateCommand();
      command.insertBean("insertedBean");
      final HibernateBean bean = command.getBean("insertedBean");
      assertNotNull(bean);
      assertEquals("insertedBean", bean.getCode());
   }

   public void testUnsuccessfullyTransaction() throws Exception {
      Exception ex = null;
      final HibernateCommand command = new HibernateCommand();
      try {
         command.insertBeans();
      } catch (Exception e) {
         ex = e;
      }
      assertNotNull(ex);
      assertTrue(ex instanceof RuntimeException);
      assertEquals("Unit Test Exception", ex.getMessage());

      final List list = command.getBeans();
      assertTrue(list.isEmpty());
   }

   public void testExceptionPropagation() {
      Exception ex = null;
      final HibernateCommand command = new HibernateCommand();
      try {
         command.someException();
      } catch (Exception e) {
         ex = e;
      }
      assertNotNull(ex);
      assertTrue(ex instanceof UnsupportedOperationException);
      assertEquals("Unit Test Exception", ex.getMessage());
   }

   public static class HibernateCommand extends AbstractHibernateCommand {

      @Remotable
      public HibernateBean getBean(final String code) throws Exception {
         final Query<HibernateBean> query = getSession().createQuery(
               "from HibernateBean bean where bean.code = :code",
               HibernateBean.class);
         query.setParameter("code", code);
         return (HibernateBean) query.uniqueResult();
      }

      @Remotable
      public List getBeans() throws Exception {
         final Query<HibernateBean> query = getSession().createQuery(
               "from HibernateBean", HibernateBean.class);
         return query.list();
      }

      @Remotable
      public HibernateBean getNonExistentBean(Class clazz) throws Exception {
         HibernateBean bean = (HibernateBean)getSession().getReference(clazz,
                 Long.MIN_VALUE);
         Hibernate.initialize(bean);
         return bean;
      }

      @Remotable
      public void someException() throws Exception {
         throw new UnsupportedOperationException("Unit Test Exception");
      }

      @Transactional
      public void insertBean(final String code) throws Exception {
         final HibernateBean bean = new HibernateBean();
         bean.setCode(code);
         getSession().persist(bean);
      }
      
      @Transactional
      public void insertBeans() throws Exception {
         final HibernateBean bean = new HibernateBean();
         final HibernateBean bean2 = new HibernateBean();
         final HibernateBean bean3 = new HibernateBean();

         bean.setCode("first bean");
         bean2.setCode("second bean");
         bean3.setCode("third bean");

         getSession().persist(bean);
         getSession().persist(bean2);
         getSession().persist(bean3);
         getSession().flush();

         throw new RuntimeException("Unit Test Exception");
      }

      @Transactional
      public void deleteAll() throws Exception {
          getSession().createMutationQuery("delete from HibernateBean").executeUpdate();
      }
   }
}