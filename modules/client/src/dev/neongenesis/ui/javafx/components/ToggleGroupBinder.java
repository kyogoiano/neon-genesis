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

import dev.neongenesis.helpers.EnumHelper;
import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.GroupBinder;
import dev.neongenesis.ui.javafx.JavaFXBinder;
import dev.neongenesis.ui.metadata.FieldMetadata;
import java.util.Objects;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

/**
 * Binds an OpenJFX {@link ToggleGroup} to a form field using each {@link Toggle}'s
 * {@link Toggle#getUserData()} or {@code Node.getId()}.
 */
public class ToggleGroupBinder implements GroupBinder {

   @Override
   public BoundField bind(AbstractBinder binder, Object group, FieldMetadata fieldMetadata) {
      return new ToggleGroupBoundField((JavaFXBinder) binder, (ToggleGroup) group, fieldMetadata);
   }

   public static class ToggleGroupBoundField implements BoundField {
      private final JavaFXBinder binder;
      private final ToggleGroup group;
      private final FieldMetadata fieldMetadata;
      private final ChangeListener<Toggle> listener;
      private boolean updating;

      public ToggleGroupBoundField(JavaFXBinder binder, ToggleGroup group,
            FieldMetadata fieldMetadata) {
         this.binder = binder;
         this.group = group;
         this.fieldMetadata = fieldMetadata;
         this.listener = (obs, oldToggle, newToggle) -> {
            if (!updating) {
               updateForm();
            }
         };
         this.group.selectedToggleProperty().addListener(this.listener);
      }

      protected Object getToggleValue(Toggle toggle) {
         if (toggle == null) {
            return null;
         }
         if (toggle.getUserData() != null) {
            return toggle.getUserData();
         }
         if (toggle instanceof Node node && node.getProperties().get(JavaFXBinder.BUTTON_GROUP_SELECTION_VALUE_KEY) != null) {
            return node.getProperties().get(JavaFXBinder.BUTTON_GROUP_SELECTION_VALUE_KEY);
         }
         if (toggle instanceof Node node) {
            return node.getId();
         }
         return null;
      }

      @Override
      public String getValue() {
         Object val = getToggleValue(group.getSelectedToggle());
         return val == null ? null : val.toString();
      }

      @Override
      public void setValue(Object value) {
         updating = true;
         try {
            Object target = EnumHelper.getInstance().isEnum(value)
                  ? EnumHelper.getInstance().getName(value)
                  : value;
            for (Toggle toggle : group.getToggles()) {
               Object toggleVal = getToggleValue(toggle);
               Object normalizedToggleVal = EnumHelper.getInstance().isEnum(toggleVal)
                     ? EnumHelper.getInstance().getName(toggleVal)
                     : toggleVal;
               if (Objects.equals(target, normalizedToggleVal) || Objects.equals(value, toggleVal)) {
                  group.selectToggle(toggle);
                  return;
               }
            }
            group.selectToggle(null);
         } finally {
            updating = false;
         }
      }

      public void updateForm() {
         binder.populateForm(fieldMetadata, getToggleValue(group.getSelectedToggle()));
      }

      @Override
      public void setEnabled(boolean enabled) {
         for (Toggle toggle : group.getToggles()) {
            if (toggle instanceof Node node) {
               node.setDisable(!enabled);
            }
         }
      }

      @Override
      public void setVisible(boolean visible) {
         for (Toggle toggle : group.getToggles()) {
            if (toggle instanceof Node node) {
               node.setVisible(visible);
            }
         }
      }

      @Override
      public String getName() {
         return fieldMetadata.getFieldName();
      }

      @Override
      public void unbind() {
         group.selectedToggleProperty().removeListener(listener);
      }
   }
}
