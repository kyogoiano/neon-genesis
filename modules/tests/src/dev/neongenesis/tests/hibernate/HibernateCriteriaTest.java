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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import dev.neongenesis.annotation.Criteria;
import dev.neongenesis.annotation.Form;
import dev.neongenesis.command.hibernate.AbstractHibernateCriteria;
import dev.neongenesis.command.hibernate.CriteriaResolver;
import dev.neongenesis.helpers.CriteriaPropertyHelper;
import dev.neongenesis.tests.TestCase;
import org.hibernate.criterion.Restrictions;


public class HibernateCriteriaTest extends TestCase {

   private final DbActions dbActions = new DbActions();

   public void tearDown() throws Exception {
      dbActions.deleteAll();
   }

   public void test() throws Exception {
      dbActions.insert(3);
      CriteriaTester tester = new CriteriaTester();
      HibernateBeanForm form = new HibernateBeanForm();
      form.setCode("");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      List results = tester.find();
      assertEquals(results.size(), 3);

      form.setCode("2");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      results = tester.find();
      for (Iterator iter = results.iterator(); iter.hasNext();) {
         System.out.println(iter.next());
         
      }
      assertEquals(results.size(), 1);

      form.setCode("abc");
      CriteriaPropertyHelper.fillCriteria(tester, form);
      results = tester.find();
      assertTrue(results.isEmpty());
   }

   public void testCriteriaIntroduction() throws Exception {
      CriteriaTester c1 = new CriteriaTester();
      CriteriaTester c2 = new CriteriaTester();

      Map map = new HashMap();
      ((CriteriaResolver)c1).setPropertiesMap(map);

      assertNotSame(map, ((CriteriaResolver)c2).getPropertiesMap());
   }

   public void testCriteriaCallWithoutPropertiesMap() throws Exception {
      try {
         new CriteriaTester().find();
         fail();
      } catch (IllegalArgumentException iae) {
         // expected
      }
   }

   public static class CriteriaTester extends AbstractHibernateCriteria {

      public void setCode(String code) {
         getCriteria().add(Restrictions.eq("code", code));
      }

      @Criteria(dev.neongenesis.tests.hibernate.HibernateBean.class)
      public List find() throws Exception {
         return getCriteria().list();
      }

   }

   @Form
   public static class HibernateBeanForm {

      private Long pk;

      private String code;

      public String getCode() {
         return code;
      }

      public void setCode(String code) {
         this.code = code;
      }

      public Long getPk() {
         return pk;
      }

      public void setPk(Long pk) {
         this.pk = pk;
      }
   }

}