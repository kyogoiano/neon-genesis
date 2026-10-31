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

import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.javafx.JavaFXBinder;
import dev.neongenesis.ui.metadata.FieldMetadata;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;

/**
 * Binds OpenJFX {@link ToggleButton} ({@code RadioButton}, {@code ToggleButton})
 * and {@link CheckBox} controls to boolean form fields.
 */
public class ToggleButtonNodeBinder extends AbstractNodeBinder {

   @Override
   public BoundField bind(JavaFXBinder binder, Node node, FieldMetadata fieldMetadata) {
      return new ToggleButtonBoundField(binder, node, fieldMetadata);
   }

   public class ToggleButtonBoundField extends AbstractBoundNodeMember implements BoundField {
      private final BooleanProperty selectedProperty;
      private final FieldMetadata fieldMetadata;
      private final ChangeListener<Boolean> listener;
      private boolean updating;

      public ToggleButtonBoundField(JavaFXBinder binder, Node node,
            FieldMetadata fieldMetadata) {
         super(binder, node);
         this.fieldMetadata = fieldMetadata;
         if (node instanceof ToggleButton tb) {
            this.selectedProperty = tb.selectedProperty();
         } else if (node instanceof CheckBox cb) {
            this.selectedProperty = cb.selectedProperty();
         } else {
            throw new IllegalArgumentException("Expected ToggleButton or CheckBox, got: " + node);
         }
         this.listener = (obs, oldVal, newVal) -> {
            if (!updating) {
               updateForm();
            }
         };
         this.selectedProperty.addListener(this.listener);
      }

      @Override
      public String getValue() {
         return Boolean.toString(selectedProperty.get());
      }

      @Override
      public void setValue(Object value) {
         updating = true;
         try {
            Boolean b = (Boolean) getBinder().getConverter(fieldMetadata).convert(Boolean.class, value);
            selectedProperty.set(Boolean.TRUE.equals(b));
         } finally {
            updating = false;
         }
      }

      public void updateForm() {
         getBinder().populateForm(fieldMetadata, Boolean.valueOf(selectedProperty.get()));
      }

      @Override
      public void unbind() {
         selectedProperty.removeListener(listener);
      }
   }
}
