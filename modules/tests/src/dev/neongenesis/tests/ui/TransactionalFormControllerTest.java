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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import dev.neongenesis.annotation.Action;
import dev.neongenesis.annotation.CallWhen;
import dev.neongenesis.annotation.Form;
import dev.neongenesis.tests.TestCase;
import dev.neongenesis.ui.ActionInvoker;
import dev.neongenesis.ui.controller.DefaultFormController;
import dev.neongenesis.ui.controller.FormController;
import dev.neongenesis.ui.controller.FormControllerListener;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;
import org.apache.commons.beanutils.BeanUtils;

public class TransactionalFormControllerTest extends TestCase {

   @Form
   public static class MainForm {
      private String fieldString;

      public String getFieldString() {
         return fieldString;
      }

      public void setFieldString(String fieldString) throws Exception {
         this.fieldString = fieldString;

         if ("value2".equals(fieldString)) {
            throw new Exception();
         }
      }

      @Action
      @CallWhen("genesis.hasChanged('form:fieldString')")
      public void action1() throws Exception {
         ActionInvoker.invoke(this, "action2");
      }

      @Action
      public void action2() {
      }
   }

   @Form
   public static class ComponentForm {
      private String fieldString;

      public String getFieldString() {
         return fieldString;
      }

      public void setFieldString(String fieldString) throws Exception {
         this.fieldString = fieldString;
      }
   }

   class FormControllerAdapter implements FormControllerListener {
      public void enabledConditionsChanged(Map updatedEnabledConditions) {
      }

      public void visibleConditionsChanged(Map updatedVisibleConditions) {
      }

      public boolean beforeInvokingMethod(MethodMetadata methodMetadata) throws
            Exception {
         return true;
      }

      public void afterInvokingMethod(MethodMetadata methodMetadata) throws
            Exception {
      }

      public void dataProvidedListChanged(DataProviderMetadata metadata,
            List items)
            throws Exception {
      }

      public void dataProvidedIndexesChanged(DataProviderMetadata metadata,
            int[] selectedIndexes) {
      }

      public void valuesChanged(Map updatedValues) throws Exception {
      }
   }

   private FormController getController(Object form) throws Exception {
      final FormController formController = new DefaultFormController();
      formController.setFormMetadata(getFormMetadata(form));
      formController.setForm(form);
      formController.setup();
      return formController;
   }

   public void testSimplePopulate() throws Exception {
      final ComponentForm formComponent = new ComponentForm();
      final FormController controllerComponent = getController(formComponent);

      final MainForm formMain = new MainForm();
      final FormController controllerMain = getController(formMain);

      final Map valueNull = Collections.singletonMap("fieldString", null);
      final Map value1 = Collections.singletonMap("fieldString", "value1");
      final Map value2 = Collections.singletonMap("fieldString", "value2");
      final Map value3 = Collections.singletonMap("fieldString", "value3");

      controllerComponent.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerMain.populate(updatedValues, null);
            }
         }
      });
      controllerMain.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerComponent.populate(updatedValues, null);
            }
         }
      });


      {
         controllerComponent.populate(value1, null);
         controllerComponent.populate(value1, null);
         try {
            controllerComponent.populate(value2, null);
         } catch (Exception exception) {
            //Expected...
         }

         final Map formMainData = BeanUtils.describe(formMain);
         final Map formComponentData = BeanUtils.describe(formComponent);
         assertDescribedMapEquals(value1, formComponentData);
         assertDescribedMapEquals(formComponentData, formMainData);
      }

      {
         controllerComponent.populate(value3, null);
         try {
            controllerComponent.populate(value2, null);
         } catch (Exception exception) {
            //Expected...
         }

         final Map formMainData = BeanUtils.describe(formMain);
         final Map formComponentData = BeanUtils.describe(formComponent);
         assertDescribedMapEquals(value3, formComponentData);
         assertDescribedMapEquals(formComponentData, formMainData);
      }

      {
         controllerComponent.populate(value1, null);
         controllerComponent.populate(value3, null);
         controllerComponent.populate(valueNull, null);
         try {
            controllerComponent.populate(value2, null);
         } catch (Exception exception) {
            //Expected...
         }

         final Map formMainData = BeanUtils.describe(formMain);
         final Map formComponentData = BeanUtils.describe(formComponent);
         assertDescribedMapEquals(valueNull, formComponentData);
         assertDescribedMapEquals(formComponentData, formMainData);
      }
   }

   public void testRecursiveAction() throws Exception {
      final ComponentForm formComponent = new ComponentForm();
      final FormController controllerComponent = getController(formComponent);

      final MainForm formMain = new MainForm();
      final FormController controllerMain = getController(formMain);

      final Map value1 = Collections.singletonMap("fieldString", "value1");

      controllerComponent.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerMain.populate(updatedValues, null);
            }
         }
      });
      controllerMain.addFormControllerListener(new FormControllerAdapter() {
         public void valuesChanged(Map updatedValues) throws Exception {
            if (updatedValues.containsKey("fieldString")) {
               controllerComponent.populate(updatedValues, null);
            }
         }
      });

      // Should not throw a StackOverflowError or something like that
      controllerComponent.populate(value1, null);
   }
}
