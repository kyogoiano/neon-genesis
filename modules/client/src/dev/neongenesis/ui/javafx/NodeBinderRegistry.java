/*
 * Neon Genesis
 * Copyright (C) 2026 Leandro Cardoso de Andrade (@kyogoiano <leandro.ueg@gmail.com>)
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
package dev.neongenesis.ui.javafx;

import dev.neongenesis.registry.Registry;
import dev.neongenesis.ui.binding.GroupBinder;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.javafx.components.AbstractNodeBinder;
import dev.neongenesis.ui.javafx.components.ComboBoxNodeBinder;
import dev.neongenesis.ui.javafx.components.LabeledNodeBinder;
import dev.neongenesis.ui.javafx.components.ListViewNodeBinder;
import dev.neongenesis.ui.javafx.components.ProgressBarNodeBinder;
import dev.neongenesis.ui.javafx.components.SliderNodeBinder;
import dev.neongenesis.ui.javafx.components.TableViewNodeBinder;
import dev.neongenesis.ui.javafx.components.TextInputControlNodeBinder;
import dev.neongenesis.ui.javafx.components.ToggleButtonNodeBinder;
import dev.neongenesis.ui.javafx.components.ToggleGroupBinder;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;

/**
 * Registry of OpenJFX {@link WidgetBinder} implementations keyed by JavaFX control class.
 */
public class NodeBinderRegistry {
   private static final NodeBinderRegistry instance = new NodeBinderRegistry();
   private final Registry registry = new Registry();
   private GroupBinder groupBinder = new ToggleGroupBinder();

   private NodeBinderRegistry() {
      register(TextInputControl.class, new TextInputControlNodeBinder());
      register(Labeled.class, new LabeledNodeBinder());
      register(ButtonBase.class, new AbstractNodeBinder() {});
      ToggleButtonNodeBinder toggleBinder = new ToggleButtonNodeBinder();
      register(ToggleButton.class, toggleBinder);
      register(CheckBox.class, toggleBinder);
      register(ComboBox.class, new ComboBoxNodeBinder());
      register(ListView.class, new ListViewNodeBinder());
      register(TableView.class, new TableViewNodeBinder());
      register(Slider.class, new SliderNodeBinder());
      register(ProgressIndicator.class, new ProgressBarNodeBinder());
   }

   public static NodeBinderRegistry getInstance() {
      return instance;
   }

   public void deregister() {
      deregisterToggleGroupBinder();
      registry.deregister();
   }

   public void deregister(Class<?> clazz) {
      registry.deregister(clazz);
   }

   public WidgetBinder register(Class<?> clazz, WidgetBinder binder) {
      return (WidgetBinder) registry.register(clazz, binder);
   }

   public WidgetBinder get(Class<?> clazz) {
      return (WidgetBinder) registry.get(clazz);
   }

   public WidgetBinder get(Class<?> clazz, boolean superClass) {
      return (WidgetBinder) registry.get(clazz, superClass);
   }

   public WidgetBinder get(Object o) {
      return (WidgetBinder) registry.get(o);
   }

   public GroupBinder registerToggleGroupBinder(GroupBinder groupBinder) {
      return this.groupBinder = groupBinder;
   }

   public GroupBinder getToggleGroupBinder() {
      return this.groupBinder;
   }

   public void deregisterToggleGroupBinder() {
      this.groupBinder = null;
   }
}
