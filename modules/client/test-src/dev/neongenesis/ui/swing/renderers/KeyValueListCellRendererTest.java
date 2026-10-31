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
package dev.neongenesis.ui.swing.renderers;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.swing.SwingBinder;
import dev.neongenesis.ui.swing.components.SwingUtils;

public class KeyValueListCellRendererTest extends GenesisTestCase {
   public KeyValueListCellRendererTest() {
      super("KeyValueListCellRenderer Unit Test");
   }

   public void testConstructor() {
      new KeyValueListCellRenderer(new JComboBox());
      try {
         new KeyValueListCellRenderer(null);
         fail("IllegalArgumentException must be thrown when component is null");
      } catch (IllegalArgumentException iae) {
         // expected
      }
   }

   public void testFormat() {
      KeyValueListCellRenderer renderer = new KeyValueListCellRenderer(
            new JComboBox<>());
      assertEquals("1", renderer.format("1"));
      assertEquals("1", renderer.format(1));
      assertEquals(" ", renderer.format(""));
      assertEquals(" ", renderer.format(null));
   }

   public void testFormatWithBinder() {
      JComboBox combo = combo = SwingUtils.newCombo();
      combo.setName("someDataProvider");
      JPanel panel = new JPanel();
      panel.add(combo);
      SwingBinder binder = new SwingBinder(panel, new MockForm());
      binder.bind();

      KeyValueListCellRenderer renderer = new KeyValueListCellRenderer(combo);
      assertEquals("1", renderer.format("1"));
      assertEquals("1", renderer.format(1));
      assertEquals(" ", renderer.format(""));
      assertEquals(" ", renderer.format(null));
   }
}
