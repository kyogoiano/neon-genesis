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
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.javafx.JavaFXBinder;
import dev.neongenesis.ui.metadata.FieldMetadata;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

/**
 * Binds OpenJFX {@link TextInputControl} ({@code TextField}, {@code PasswordField},
 * {@code TextArea}) to form fields, supporting both {@code AS_YOU_TYPE} and
 * default focus-lost binding strategies.
 */
public class TextInputControlNodeBinder extends AbstractNodeBinder {
   private final boolean trim;

   public TextInputControlNodeBinder() {
      this(true);
   }

   public TextInputControlNodeBinder(boolean trim) {
      this.trim = trim;
   }

   protected boolean isTrim() {
      return trim;
   }

   @Override
   public BoundField bind(JavaFXBinder binder, Node node, FieldMetadata fieldMetadata) {
      return new TextInputBoundField(binder, (TextInputControl) node, fieldMetadata);
   }

   public class TextInputBoundField extends AbstractBoundNodeMember implements BoundField {
      private final TextInputControl control;
      private final FieldMetadata fieldMetadata;
      private final boolean asYouType;
      private final ChangeListener<String> textListener;
      private final ChangeListener<Boolean> focusListener;
      private boolean updating;

      public TextInputBoundField(JavaFXBinder binder, TextInputControl control,
            FieldMetadata fieldMetadata) {
         super(binder, control);
         this.control = control;
         this.fieldMetadata = fieldMetadata;
         String strategyName = binder.getBindingStrategy(
               (String) control.getProperties().get(AbstractBinder.BINDING_STRATEGY_PROPERTY));
         this.asYouType = AbstractBinder.BINDING_STRATEGY_AS_YOU_TYPE.equals(strategyName);

         if (this.asYouType) {
            this.textListener = (obs, oldVal, newVal) -> {
               if (!updating) {
                  updating = true;
                  try {
                     updateForm();
                  } finally {
                     updating = false;
                  }
               }
            };
            this.focusListener = null;
            this.control.textProperty().addListener(this.textListener);
         } else {
            this.textListener = null;
            this.focusListener = (obs, wasFocused, isNowFocused) -> {
               if (Boolean.TRUE.equals(wasFocused) && Boolean.FALSE.equals(isNowFocused)) {
                  updateForm();
               }
            };
            this.control.focusedProperty().addListener(this.focusListener);
         }
      }

      public void updateForm() {
         getBinder().populateForm(fieldMetadata, getValue());
      }

      @Override
      public String getValue() {
         String text = control.getText();
         if (text == null) {
            return "";
         }
         return isTrim() ? text.trim() : text;
      }

      public String formatValue(Object value) {
         return getBinder().getFormatter(fieldMetadata).format(value);
      }

      @Override
      public void setValue(Object value) {
         String formatted = formatValue(value);
         if (updating && fieldMetadata.getEqualityComparator().equals(formatted, getValue())) {
            return;
         }
         boolean prev = updating;
         updating = true;
         try {
            control.setText(formatted);
         } finally {
            updating = prev;
         }
      }

      @Override
      public void unbind() {
         if (textListener != null) {
            control.textProperty().removeListener(textListener);
         }
         if (focusListener != null) {
            control.focusedProperty().removeListener(focusListener);
         }
      }
   }
}
