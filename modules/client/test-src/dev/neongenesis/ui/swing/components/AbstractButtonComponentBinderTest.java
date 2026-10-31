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
package dev.neongenesis.ui.swing.components;

import javax.swing.AbstractButton;
import javax.swing.JPanel;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;

public class AbstractButtonComponentBinderTest extends GenesisTestCase {
   private AbstractButton button;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private MockForm form;
   private ActionMetadata actionMeta;
   private FieldMetadata fieldMeta;
   private BoundField boundField;
   
   public AbstractButtonComponentBinderTest() {
      super("Abstract Button Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      button = SwingUtils.newButton();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      actionMeta = (ActionMetadata) form.getFormMetadata().getActionMetadatas().get("someAction");
      fieldMeta = (FieldMetadata) form.getFormMetadata().getFieldMetadata("stringField");
      componentBinder = binder.getWidgetBinder(button);
   }

   public void testStandardBehaviour() {
      assertTrue(componentBinder instanceof AbstractButtonComponentBinder);
      assertNull(componentBinder.bind(binder, button,
            (DataProviderMetadata) null));
   }

   public void testButtonClick() {
      assertNotNull(componentBinder.bind(binder, button, actionMeta));

      button.doClick();
      assertSame(actionMeta, binder.get("invokeFormAction(ActionMetadata)"));
   }
   
   public void testText() throws Exception {
      assertNotNull(boundField = componentBinder.bind(binder, button, fieldMeta));
      assertEquals("", button.getText());

      boundField.setValue("someValue");
      assertEquals("someValue", button.getText());

      boundField.setValue(null);
      assertEquals("", button.getText());
   }
}
