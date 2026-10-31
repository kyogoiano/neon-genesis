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
package dev.neongenesis.text;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockBean;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.controller.FormControllerRegistry;
import dev.neongenesis.ui.controller.MockFormController;
import dev.neongenesis.ui.metadata.FormMetadataRegistry;

public class ScriptFormatterTest extends GenesisTestCase {
   public void testFormWithoutMetadata() {
      try {
         new ScriptFormatter(new MockForm(), "");
         fail("Form has no metadata and still worked");
      } catch (IllegalArgumentException iae) {
         // expected
      }   
   }

   public void testFormWithoutController() {
      try {
         MockForm form = new MockForm();
         FormMetadataRegistry.getInstance().register(MockForm.class, 
               form.getFormMetadata());
         new ScriptFormatter(form, "");
         fail("Form has no controller and still worked");
      } catch (IllegalArgumentException iae) {
         // expected
      }   
   }

   public void testConstructorWithWorkingForm() {
      createFormatter(false);
   }

   private ScriptFormatter createFormatter(boolean setup) {
      MockForm form = createForm(setup);

      return new ScriptFormatter(form, "concat($o/key, ' - ', $o/value)");
   }

   private MockForm createForm(boolean setup) {
      MockForm form = new MockForm();
      FormMetadataRegistry.getInstance().
            register(MockForm.class, form.getFormMetadata());
      MockFormController controller = form.getController();
      FormControllerRegistry.getInstance().register(form, controller);

      if (setup) {
         try {
            controller.setup();
            controller.put(MockFormController.SCRIPT_CONTEXT, form.
                  getFormMetadata().getScript().newContext(form));
         } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
         }
      }

      return form;
   }

   public void testFormatWithNonSetupController() {
      try {
         createFormatter(false).format(null);
         fail("Should throw a IllegalStateException since form has not been " +
               "setup");
      } catch (IllegalStateException ise) {
         // expected
      }   
   }

   public void testFormatWithValidObject() {
      assertEquals("test - Test", createFormatter(true).format(new MockBean(
            "test", "Test")));
   }

   public void testFormatWithRegisteredAlias() {
      try {
         MockForm form = createForm(true);
         form.getController().getScriptContext().declare("aVar", form);
         new ScriptFormatter(form, "$aVar/key", "aVar").format(new MockBean(
               "test", "Test"));
         fail("Should throw a IllegalStateException since aVar is already " +
               "declared");
      } catch (IllegalStateException ise) {
         // expected
      }
   }
}
