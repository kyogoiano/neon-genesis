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
import javax.swing.JSlider;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;

public class JSliderComponentBinderTest extends GenesisTestCase {
   private JSlider slider;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public JSliderComponentBinderTest() {
      super("JSlider Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      slider = new JSlider();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      componentBinder = binder.getWidgetBinder(slider);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }

   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JSliderComponentBinder);
      
      assertNull(componentBinder.bind(binder, slider, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, slider,
            (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, slider, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue(3);
      assertEquals(3, slider.getValue());

      boundField.setValue(100);
      assertEquals(100, slider.getValue());
   }

   public void testUpdateValue() {
      assertNull(componentBinder.bind(binder, slider, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, slider,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, slider, fieldMeta));

      slider.setValue(3);
      Integer value = (Integer) binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(3, value.intValue());

      slider.setValue(100);
      value = (Integer)  binder.get("populateForm(FieldMetadata,Object)");
      assertNotNull(value);
      assertEquals(100, value.intValue());
   }
}
