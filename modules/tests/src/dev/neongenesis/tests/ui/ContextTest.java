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
package dev.neongenesis.tests.ui;

import java.util.HashMap;
import java.util.Map;

import dev.neongenesis.annotation.Condition;
import dev.neongenesis.annotation.Form;
import dev.neongenesis.tests.TestCase;
import dev.neongenesis.ui.controller.DefaultFormController;
import dev.neongenesis.ui.controller.FormController;

public class ContextTest extends TestCase {

   private FormController getController(Object form) throws Exception {
      final FormController formController = new DefaultFormController();
      formController.setFormMetadata(getFormMetadata(form));
      formController.setForm(form);
      formController.setup();
      return formController;
   }

   public void testContext() throws Exception {
      final FooForm form = new FooForm();
      final FormController controller = getController(form);

      assertEquals(form.context.get("someVar"), Boolean.TRUE);

      final Map newValues = new HashMap();
      newValues.put("string1", " a342 23");
      controller.populate(newValues, null);

      assertEquals(form.context.get("someVar"), Boolean.FALSE);
   }

   @Form
   public static class FooForm {
      private Map context;
      private String string1;

      public void setContext(Map context) {
         this.context = context;
      }

      @Condition("someVar=genesis.isEmpty('form:string1')")
      public String getString1() {
         return string1;
      }

      public void setString1(String string1) {
         this.string1 = string1;
      }
   }
}
