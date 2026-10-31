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
package dev.neongenesis.helpers;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockScriptFactory;
import dev.neongenesis.script.ScriptRegistry;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;

public class CriteriaPropertyHelperTest extends GenesisTestCase {
   public static class MockHibernateCriteria extends dev.neongenesis.mockobjects.MockHibernateCriteria  {
      private String property1;
      private String property2;
      private String criteriaProperty;

      public String getProperty1() {
         return property1;
      }

      public void setProperty1(String property1) {
         this.property1 = property1;
      }

      public String getProperty2() {
         return property2;
      }

      public void setProperty2(String property2) {
         this.property2 = property2;
      }

      public String getCriteriaProperty() {
         return criteriaProperty;
      }

      public void setCriteriaProperty(String criteriaProperty) {
         this.criteriaProperty = criteriaProperty;
      }
   }

   public static class MockForm extends dev.neongenesis.mockobjects.MockForm {
      private String property1;
      private String property2;
      private String formProperty;

      public String getProperty1() {
         return property1;
      }

      public void setProperty1(String property1) {
         this.property1 = property1;
      }

      public String getProperty2() {
         return property2;
      }

      public void setProperty2(String property2) {
         this.property2 = property2;
      }

      public String getFormProperty() {
         return formProperty;
      }

      public void setFormProperty(String formProperty) {
         this.formProperty = formProperty;
      }
   }

   public static class MockFormNoMetadata {
      private String property1;
      private String property2;
      private String formProperty;

      public String getProperty1() {
         return property1;
      }

      public void setProperty1(String property1) {
         this.property1 = property1;
      }

      public String getProperty2() {
         return property2;
      }

      public void setProperty2(String property2) {
         this.property2 = property2;
      }

      public String getFormProperty() {
         return formProperty;
      }

      public void setFormProperty(String formProperty) {
         this.formProperty = formProperty;
      }
   }

   public void testFillCriteria() throws Exception {
      MockHibernateCriteria criteria = new MockHibernateCriteria();
      MockForm form = new MockForm();

      FormMetadata metadata = form.getFormMetadata();
      metadata.addFieldMetadata("property1", new FieldMetadata("property1", 
            String.class, true));

      // Simple test with "incomplete" metadata
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertTrue(criteria.getPropertiesMap().isEmpty());

      // Tests for correct behaviour when a field is empty
      String value = new String();
      form.setProperty1(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(0, criteria.getPropertiesMap().size());

      // Tests for correct behaviour when a field is populated
      value = "value";
      form.setProperty1(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(1, criteria.getPropertiesMap().size());
      assertSame(value, criteria.getPropertiesMap().get("property1"));

      // Tests for correct behaviour when a field with no FieldMetadata is filled
      form.setProperty1(null);
      form.setProperty2(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(1, criteria.getPropertiesMap().size());
      assertSame(value, criteria.getPropertiesMap().get("property2"));

      // Fields that don't exist in both classes shouldn't be copied
      form.setProperty2(null);
      form.setFormProperty(value);
      criteria.setCriteriaProperty(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertTrue(criteria.getPropertiesMap().isEmpty());
   }
   
   public void testFillCriteriaNoMetadata() throws Exception {
      MockHibernateCriteria criteria = new MockHibernateCriteria();
      MockFormNoMetadata form = new MockFormNoMetadata();

      ScriptRegistry.getInstance().setScriptFactoryName(MockScriptFactory.class.getName());

      // Simple test with "incomplete" metadata
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertTrue(criteria.getPropertiesMap().isEmpty());
      
      // Tests for correct behaviour when a field is empty
      String value = new String();
      form.setProperty1(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(0, criteria.getPropertiesMap().size());
      
      // Tests for correct behaviour when a field is populated
      value = "value";
      form.setProperty1(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(1, criteria.getPropertiesMap().size());
      assertSame(value, criteria.getPropertiesMap().get("property1"));
      
      // Tests for correct behaviour when a field with no FieldMetadata is filled
      form.setProperty1(null);
      form.setProperty2(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertEquals(1, criteria.getPropertiesMap().size());
      assertSame(value, criteria.getPropertiesMap().get("property2"));
      
      // Fields that don't exist in both classes shouldn't be copied
      form.setProperty2(null);
      form.setFormProperty(value);
      criteria.setCriteriaProperty(value);
      CriteriaPropertyHelper.fillCriteria(criteria, form);
      assertTrue(criteria.getPropertiesMap().isEmpty());
   }
}