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
import javax.swing.JProgressBar;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;

public class JProgressBarComponentBinderTest extends GenesisTestCase {
   private JProgressBar progress;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   
   public JProgressBarComponentBinderTest() {
      super("JProgressBar Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      progress = new JProgressBar();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      componentBinder = binder.getWidgetBinder(progress);
      fieldMeta = form.getFormMetadata().getFieldMetadata("intField");
   }
   
   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JProgressBarComponentBinder);
      
      assertNull(componentBinder.bind(binder, progress, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, progress, (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, progress, fieldMeta);
      assertNotNull(boundField);

      assertEquals(0, progress.getValue());

      boundField.setValue(3);
      assertEquals(3, progress.getValue());

      boundField.setValue(100);
      assertEquals(100, progress.getValue());
   }
}
