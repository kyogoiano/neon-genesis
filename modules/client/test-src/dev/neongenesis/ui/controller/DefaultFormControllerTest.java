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
package dev.neongenesis.ui.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;
import dev.neongenesis.ui.swing.SwingBinder;

public class DefaultFormControllerTest extends GenesisTestCase {
   private DefaultFormController controller;
   private MockForm form;
   
   public DefaultFormControllerTest() {
      super("DefaultFormController Unit Test");
   }

   protected void setUp() throws Exception {
      controller = new DefaultFormController();
      form = new MockForm();
      controller.setForm(form);
      controller.setFormMetadata(form.getFormMetadata());
   }

   public void testSetup() throws Throwable {
      final Map map = new HashMap();
      controller = new DefaultFormController() {
         protected void evaluate(boolean firstCall) throws Exception {
            map.put("evaluate(firstCall)", Boolean.valueOf(firstCall));
         }
      };
      controller.setForm(form);
      controller.setFormMetadata(form.getFormMetadata());
      controller.setup();

      // Assert controller is setup
      assertTrue(controller.isSetup());
      // Assert controller was evaluated
      assertEquals(Boolean.TRUE, map.get("evaluate(firstCall)"));
      // Assert Script context was created
      assertNotNull("Script context is null", controller.getScriptContext());
      // Assert FormState was created
      assertNotNull("No FormState", controller.getFormState());
      // Assert VariablesMap and Form's Context are the same object
      assertSame(controller.getScriptContext().getContextMap(), form.getContext());
      // Assert FormState is declared on script context
      assertSame(controller.getFormState(), controller.getScriptContext().getContextMap().get(
            FormController.CURRENT_STATE_KEY));
      // Assert FormMetadata is declared on script context
      assertSame(controller.getFormMetadata(), controller.getScriptContext().getContextMap().get(
            FormController.FORM_METADATA_KEY));

      Exception ex = null;

      try {
         controller.setup();
      } catch (Exception e) {
         ex = e;
      }

      // Assert that an IllegalStateException occured
      assertTrue(ex instanceof IllegalStateException);
   }

   public void testCreateScriptContext() {
      ScriptContext ctx = controller.createScriptContext();

      // Assert script context was created
      assertNotNull(ctx);
   }

   public void testFormControllerListeners() {
      FormControllerListener listener = new SwingBinder(new JPanel(), new Object(), form);
      
      // Assert no listeners
      assertEquals(0, controller.getFormControllerListeners().size());
      
      controller.addFormControllerListener(listener);
      assertTrue(controller.getFormControllerListeners().contains(listener));
      assertEquals(1, controller.getFormControllerListeners().size());
      
      controller.removeFormControllerListener(listener);
      assertTrue(!controller.getFormControllerListeners().contains(listener));
      assertEquals(0, controller.getFormControllerListeners().size());
      
   }

   public void testBeforeActionDataProvider() throws Exception {
      controller.setup();

      final String dataProviderName = "someDataProvider";
      final Boolean called[] = new Boolean[1];

      controller.addFormControllerListener(new FormControllerListener() {
         public void enabledConditionsChanged(Map updatedEnabledConditions) {
         }

         public void visibleConditionsChanged(Map updatedVisibleConditions) {
         }

         public boolean beforeInvokingMethod(MethodMetadata methodMetadata) 
               throws Exception {
            if (dataProviderName.equals(methodMetadata.getName())) {
               called[0] = Boolean.TRUE;
            }

            return true;
         }

         public void afterInvokingMethod(MethodMetadata methodMetadata) 
               throws Exception {
         }

         public void dataProvidedListChanged(DataProviderMetadata metadata, 
               List items) throws Exception {
         }

         public void dataProvidedIndexesChanged(DataProviderMetadata metadata, 
               int[] selectedIndexes) {
         }

         public void valuesChanged(Map updatedValues) throws Exception {
         }
      });

      controller.invokeAction(dataProviderName, Collections.EMPTY_MAP);

      assertEquals("@BeforeAction was not called for @DataProvider", 
            Boolean.TRUE, called[0]);
   }

   public void testValuesChangedIsReentrant() throws Exception {
      controller.setup();

      controller.addFormControllerListener(new FormControllerListener() {
         private boolean firstTime = true;

         public void enabledConditionsChanged(Map updatedEnabledConditions) {
         }

         public void visibleConditionsChanged(Map updatedVisibleConditions) {
         }

         public boolean beforeInvokingMethod(MethodMetadata methodMetadata) throws Exception {
            return true;
         }

         public void afterInvokingMethod(MethodMetadata methodMetadata) throws Exception {
         }

         public void dataProvidedListChanged(DataProviderMetadata metadata, List items) throws Exception {
         }

         public void dataProvidedIndexesChanged(DataProviderMetadata metadata, int[] selectedIndexes) {
         }

         public void valuesChanged(Map updatedValues) throws Exception {
            for (Iterator i = updatedValues.entrySet().iterator(); i.hasNext();) {
               Map.Entry e = (Map.Entry) i.next();

               if (firstTime && "stringField".equals(e.getKey())) {
                  firstTime = false;
                  controller.updateChangedMap(Collections.singletonMap(
                        e.getKey(), e.getValue()), false, new HashMap());
               }
            }
         }
      });

      form.setStringField("abc");
      form.setObjectField(new Object());
      controller.update();
   }
}