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

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;

public class JToggleButtonComponentBinderTest extends GenesisTestCase {
   private JToggleButton toggle;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public JToggleButtonComponentBinderTest() {
      super("JToggleButton Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      toggle = new JToggleButton();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      componentBinder = binder.getWidgetBinder(toggle);
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JToggleButtonComponentBinder);
      assertNull(componentBinder.bind(binder, toggle,
            (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, toggle, fieldMeta);
      assertNotNull(boundField);

      assertFalse(toggle.isSelected());

      boundField.setValue(Boolean.TRUE);
      assertTrue(toggle.isSelected());

      boundField.setValue(Boolean.FALSE);
      assertFalse(toggle.isSelected());
   }

   public void testUpdateValue() throws Exception {
      assertNull(componentBinder.bind(binder, toggle,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, toggle, fieldMeta));

      toggle.doClick();
      assertEquals(Boolean.TRUE, binder.get("populateForm(FieldMetadata,Object)"));

      toggle.doClick();
      assertEquals(Boolean.FALSE, binder.get("populateForm(FieldMetadata,Object)"));
   }
}
