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

import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.binding.ExceptionHandler;
import dev.neongenesis.ui.binding.GroupBinder;
import dev.neongenesis.ui.binding.LookupStrategy;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.javafx.lookup.BreadthFirstNodeLookupStrategy;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;

/**
 * {@code JavaFXBinder} is the default implementation of {@link AbstractBinder}
 * for OpenJFX 21+. It connects a JavaFX {@link Node} hierarchy (such as a
 * {@code Pane}, {@code VBox}, {@code GridPane}, or FXML root) to a Neon Genesis
 * {@code @Form} POJO and {@code @ViewHandler}.
 */
public class JavaFXBinder extends AbstractBinder {
   public static final String BINDER_KEY = "genesis:JavaFXBinder"; // NOI18N
   public static final String BUTTON_GROUP_SELECTION_VALUE_KEY = "genesis:buttonGroupSelectionValue"; // NOI18N

   private final NodeBinderRegistry factory = NodeBinderRegistry.getInstance();

   public JavaFXBinder(Node root, Object form) {
      this(root, null, form, root);
   }

   public JavaFXBinder(Node root, LookupStrategy lookupStrategy, Object form) {
      this(root, lookupStrategy, form, root);
   }

   public JavaFXBinder(Node root, Object form, Object handler) {
      this(root, null, form, handler);
   }

   public JavaFXBinder(Node root, LookupStrategy lookupStrategy, Object form, Object handler) {
      super(root, form, handler, lookupStrategy);
   }

   @Override
   protected ExceptionHandler createExceptionHandler() {
      return new JavaFXExceptionHandler((Node) getRoot());
   }

   @Override
   protected LookupStrategy createLookupStrategy() {
      return new BreadthFirstNodeLookupStrategy();
   }

   public Node register(String name, Node node) {
      return (Node) getLookupStrategy().register(name, node);
   }

   public ToggleGroup registerToggleGroup(String name, ToggleGroup toggleGroup) {
      return (ToggleGroup) registerGroup(name, toggleGroup);
   }

   public ToggleGroup registerToggleGroup(String name, ToggleGroup toggleGroup, GroupBinder groupBinder) {
      return (ToggleGroup) registerGroup(name, toggleGroup, groupBinder);
   }

   @Override
   public boolean isVirtual(Object widget) {
      return widget instanceof Node node && Boolean.TRUE.equals(node.getProperties().get(VIRTUAL));
   }

   @Override
   protected WidgetBinder getDefaultWidgetBinderFor(Object object) {
      return factory.get(object.getClass(), true);
   }

   @Override
   protected GroupBinder getDefaultGroupBinderFor(Object group) {
      return factory.getToggleGroupBinder();
   }

   @Override
   protected void markBound() {
      if (getRoot() instanceof Node node) {
         node.getProperties().put(GENESIS_BOUND, Boolean.TRUE);
      }
   }

   @Override
   protected void markUnbound() {
      if (getRoot() instanceof Node node) {
         node.getProperties().put(GENESIS_BOUND, Boolean.FALSE);
      }
   }
}
