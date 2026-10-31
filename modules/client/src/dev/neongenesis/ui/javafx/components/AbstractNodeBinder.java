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
package dev.neongenesis.ui.javafx.components;

import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.binding.BoundAction;
import dev.neongenesis.ui.binding.BoundDataProvider;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.BoundMember;
import dev.neongenesis.ui.binding.PropertyMisconfigurationException;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.javafx.JavaFXBinder;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ButtonBase;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Base {@link WidgetBinder} for OpenJFX {@link Node} controls.
 */
public abstract class AbstractNodeBinder implements WidgetBinder {

   @Override
   public BoundField bind(AbstractBinder binder, Object widget, FieldMetadata fieldMetadata) {
      return bind((JavaFXBinder) binder, (Node) widget, fieldMetadata);
   }

   @Override
   public BoundAction bind(AbstractBinder binder, Object widget, ActionMetadata actionMetadata) {
      return bind((JavaFXBinder) binder, (Node) widget, actionMetadata);
   }

   @Override
   public BoundDataProvider bind(AbstractBinder binder, Object widget,
         DataProviderMetadata dataProviderMetadata) {
      return bind((JavaFXBinder) binder, (Node) widget, dataProviderMetadata);
   }

   public BoundField bind(JavaFXBinder binder, Node node, FieldMetadata fieldMetadata) {
      return null;
   }

   public BoundAction bind(JavaFXBinder binder, Node node, ActionMetadata actionMetadata) {
      if (node instanceof ButtonBase buttonBase) {
         return new ButtonBaseBoundAction(binder, buttonBase, actionMetadata);
      }
      return null;
   }

   public BoundDataProvider bind(JavaFXBinder binder, Node node,
         DataProviderMetadata dataProviderMetadata) {
      return null;
   }

   public abstract class AbstractBoundNodeMember implements BoundMember {
      private final Node node;
      private final JavaFXBinder binder;
      private final Set<Node> enabledWidgetGroupSet = new HashSet<>();
      private final Set<Node> visibleWidgetGroupSet = new HashSet<>();

      public AbstractBoundNodeMember(JavaFXBinder binder, Node node) {
         this.binder = binder;
         this.node = node;
         createGroups();
         node.getProperties().put(JavaFXBinder.BINDER_KEY, binder);
      }

      protected JavaFXBinder getBinder() {
         return binder;
      }

      protected Node getNode() {
         return node;
      }

      protected void createGroups() {
         createGroup(node.getProperties().get(JavaFXBinder.WIDGET_GROUP_PROPERTY), true, true);
         createGroup(node.getProperties().get(JavaFXBinder.ENABLED_GROUP_PROPERTY), true, false);
         createGroup(node.getProperties().get(JavaFXBinder.VISIBLE_GROUP_PROPERTY), false, true);
      }

      protected void createGroup(Object group, boolean enabled, boolean visible) {
         if (group == null) {
            return;
         }
         if (group instanceof String || group instanceof String[]) {
            String[] names = group instanceof String s ? s.split("\\s*,\\s*") : (String[]) group;
            for (String name : names) {
               Object target = binder.lookup(name);
               if (target instanceof Node targetNode) {
                  if (enabled) {
                     enabledWidgetGroupSet.add(targetNode);
                  }
                  if (visible) {
                     visibleWidgetGroupSet.add(targetNode);
                  }
               }
            }
         } else if (group instanceof Collection<?> col) {
            for (Object item : col) {
               if (item instanceof Node targetNode) {
                  if (enabled) {
                     enabledWidgetGroupSet.add(targetNode);
                  }
                  if (visible) {
                     visibleWidgetGroupSet.add(targetNode);
                  }
               }
            }
         } else if (group instanceof Object[] arr) {
            createGroup(Arrays.asList(arr), enabled, visible);
         } else if (group instanceof Node targetNode) {
            if (enabled) {
               enabledWidgetGroupSet.add(targetNode);
            }
            if (visible) {
               visibleWidgetGroupSet.add(targetNode);
            }
         }
      }

      protected boolean isBlank(Node node) {
         return isBoolean(node, JavaFXBinder.BLANK_PROPERTY);
      }

      protected boolean isBoolean(Node node, String propertyName) {
         Object val = node.getProperties().get(propertyName);
         if (val == null) {
            return false;
         }
         if (val instanceof Boolean b) {
            return b;
         }
         if (val instanceof String s) {
            return Boolean.parseBoolean(s);
         }
         throw new PropertyMisconfigurationException(
               "Property " + propertyName + " for node " + getName() + " must be a boolean");
      }

      @Override
      public void setEnabled(boolean enabled) {
         node.setDisable(!enabled);
         for (Node groupNode : enabledWidgetGroupSet) {
            groupNode.setDisable(!enabled);
         }
      }

      @Override
      public void setVisible(boolean visible) {
         node.setVisible(visible);
         for (Node groupNode : visibleWidgetGroupSet) {
            groupNode.setVisible(visible);
         }
      }

      protected Object getProperty(Object bean, String propertyName)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
         int indexOfDot = 0;
         while ((indexOfDot = propertyName.indexOf('.', indexOfDot)) != -1) {
            if (PropertyUtils.getProperty(bean, propertyName.substring(0, indexOfDot)) == null) {
               return null;
            }
            indexOfDot += 1;
         }
         return PropertyUtils.getProperty(bean, propertyName);
      }

      protected String format(FieldMetadata fieldMetadata, Object value) {
         return binder.format(getName(), fieldMetadata.getFieldName(), value,
               binder.isVirtual(node, fieldMetadata.getFieldName()));
      }

      @Override
      public String getName() {
         return binder.getName(node);
      }

      @Override
      public void unbind() {
      }
   }

   public class ButtonBaseBoundAction extends AbstractBoundNodeMember implements BoundAction {
      private final ButtonBase button;
      private final ActionMetadata actionMetadata;
      private final EventHandler<ActionEvent> handler;

      public ButtonBaseBoundAction(JavaFXBinder binder, ButtonBase button,
            ActionMetadata actionMetadata) {
         super(binder, button);
         this.button = button;
         this.actionMetadata = actionMetadata;
         this.handler = event -> getBinder().invokeFormAction(this.actionMetadata);
         this.button.addEventHandler(ActionEvent.ACTION, this.handler);
      }

      @Override
      public void unbind() {
         button.removeEventHandler(ActionEvent.ACTION, handler);
      }
   }
}
