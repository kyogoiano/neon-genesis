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
package dev.neongenesis.ui.swing;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.swing.components.AbstractButtonComponentBinder;
import dev.neongenesis.ui.swing.components.ButtonGroupBinder;
import dev.neongenesis.ui.swing.components.JComboBoxComponentBinder;
import dev.neongenesis.ui.swing.components.JLabelComponentBinder;
import dev.neongenesis.ui.swing.components.JListComponentBinder;
import dev.neongenesis.ui.swing.components.JProgressBarComponentBinder;
import dev.neongenesis.ui.swing.components.JSliderComponentBinder;
import dev.neongenesis.ui.swing.components.JSpinnerComponentBinder;
import dev.neongenesis.ui.swing.components.JTableComponentBinder;
import dev.neongenesis.ui.swing.components.JTextComponentBinder;
import dev.neongenesis.ui.swing.components.JToggleButtonComponentBinder;

public class ComponentBinderRegistryTest extends GenesisTestCase {
   public ComponentBinderRegistryTest() {
      super("ComponentBinder Registry Unit Test");
   }

   private ComponentBinderRegistry registry;

   protected void setUp() {
      registry = ComponentBinderRegistry.getInstance();
   }

   protected void tearDown() throws Exception {
      registry.deregister();
      registry.register(JTextComponent.class, new JTextComponentBinder());
      registry.register(JLabel.class, new JLabelComponentBinder());
      registry.register(AbstractButton.class,
            new AbstractButtonComponentBinder());
      registry.register(JSlider.class, new JSliderComponentBinder());
      registry.register(JProgressBar.class, new JProgressBarComponentBinder());
      registry.register(JSpinner.class, new JSpinnerComponentBinder());
      registry
            .register(JToggleButton.class, new JToggleButtonComponentBinder());
      registry.register(JList.class, new JListComponentBinder());
      registry.register(JComboBox.class, new JComboBoxComponentBinder());
      registry.register(JTable.class, new JTableComponentBinder());
      registry.registerButtonGroupBinder(new ButtonGroupBinder());
   }

   public void testLookup() {
      assertComponentBinder(JTextComponent.class, JTextComponentBinder.class);
      assertComponentBinder(JLabel.class, JLabelComponentBinder.class);
      assertComponentBinder(AbstractButton.class,
            AbstractButtonComponentBinder.class);
      assertComponentBinder(JSlider.class, JSliderComponentBinder.class);
      assertComponentBinder(JProgressBar.class,
            JProgressBarComponentBinder.class);
      assertComponentBinder(JSpinner.class, JSpinnerComponentBinder.class);
      assertComponentBinder(JToggleButton.class,
            JToggleButtonComponentBinder.class);
      assertComponentBinder(JList.class, JListComponentBinder.class);
      assertComponentBinder(JComboBox.class, JComboBoxComponentBinder.class);
      assertComponentBinder(JTable.class, JTableComponentBinder.class);

      Object binder = registry.getButtonGroupBinder();
      assertNotNull(binder);
      assertEquals(ButtonGroupBinder.class, binder.getClass());
   }

   private void assertComponentBinder(Class lookupClass, Class binderClass) {
      Object binder = registry.get(lookupClass, true);
      assertNotNull(binder);
      assertEquals(binderClass, binder.getClass());
   }

   private void assertNullComponentBinder(Class lookupClass) {
      assertNull(registry.get(lookupClass, true));
   }

   public void testRegister() {
      final WidgetBinder binderInstance = new AbstractButtonComponentBinder();

      // Test for non-registered type
      registry.register(Comparable.class, binderInstance);
      assertSame(binderInstance, registry.get(Comparable.class, true));

      // Test for already registered type
      registry.register(JButton.class, binderInstance);
      assertSame(binderInstance, registry.get(JButton.class, true));

      final ButtonGroupBinder groupBinder = new ButtonGroupBinder();
      Object binder = registry.registerButtonGroupBinder(groupBinder);
      assertSame(groupBinder, binder);
   }

   public void testDeregister() {
      final WidgetBinder binderInstance = new AbstractButtonComponentBinder();

      assertNotNull(registry.get(JButton.class, true));
      registry.deregister(AbstractButton.class);
      assertNull(registry.get(JButton.class, true));

      // Test for already registered type
      registry.register(AbstractButton.class, binderInstance);
      assertSame(binderInstance, registry.get(JButton.class, true));

      registry.deregister(AbstractButton.class);
      assertNull(registry.get(JButton.class, true));

      registry.deregisterButtonGroupBinder();
      assertNull(registry.getButtonGroupBinder());
   }

   public void testDeregisterAll() {
      registry.deregister();

      assertNullComponentBinder(JTextComponent.class);
      assertNullComponentBinder(JLabel.class);
      assertNullComponentBinder(AbstractButton.class);
      assertNullComponentBinder(JSlider.class);
      assertNullComponentBinder(JProgressBar.class);
      assertNullComponentBinder(JSpinner.class);
      assertNullComponentBinder(JList.class);
      assertNullComponentBinder(JComboBox.class);
      assertNullComponentBinder(JTable.class);
      assertNullComponentBinder(JToggleButton.class);

      assertNull(registry.getButtonGroupBinder());
   }
}