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

import dev.neongenesis.registry.Registry;
import dev.neongenesis.ui.binding.GroupBinder;
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

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.text.JTextComponent;

public class ComponentBinderRegistry {
   private static final ComponentBinderRegistry instance =
      new ComponentBinderRegistry();
   private final Registry registry = new Registry();
   private GroupBinder groupBinder = new ButtonGroupBinder();

   private ComponentBinderRegistry() {
      register(JTextComponent.class, new JTextComponentBinder());
      register(JLabel.class, new JLabelComponentBinder());
      register(AbstractButton.class, new AbstractButtonComponentBinder());
      register(JSlider.class, new JSliderComponentBinder());
      register(JProgressBar.class, new JProgressBarComponentBinder());
      register(JSpinner.class, new JSpinnerComponentBinder());
      register(JToggleButton.class, new JToggleButtonComponentBinder());
      register(JList.class, new JListComponentBinder());
      register(JComboBox.class, new JComboBoxComponentBinder());
      register(JTable.class, new JTableComponentBinder());
   }

   public static ComponentBinderRegistry getInstance() {
      return instance;
   }

   public void deregister() {
      deregisterButtonGroupBinder();
      registry.deregister();
   }

   public void deregister(Class clazz) {
      registry.deregister(clazz);
   }

   public WidgetBinder register(Class clazz, WidgetBinder binder) {
      return (WidgetBinder) registry.register(clazz, binder);
   }

   public WidgetBinder get(Class clazz) {
      return (WidgetBinder) registry.get(clazz);
   }

   public WidgetBinder get(Class clazz, boolean superClass) {
      return (WidgetBinder) registry.get(clazz, superClass);
   }

   public WidgetBinder get(Object o) {
      return (WidgetBinder) registry.get(o);
   }

   public GroupBinder registerButtonGroupBinder(GroupBinder groupBinder) {
      return this.groupBinder = groupBinder;
   }

   public GroupBinder getButtonGroupBinder() {
      return this.groupBinder;
   }

   public void deregisterButtonGroupBinder() {
      this.groupBinder = null;
   }
}
