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
import javafx.scene.Node;
import javafx.scene.control.Labeled;

/**
 * Binds OpenJFX {@link Labeled} controls (such as {@link javafx.scene.control.Label})
 * to form fields.
 */
public class LabeledNodeBinder extends AbstractNodeBinder {

   @Override
   public BoundField bind(JavaFXBinder binder, Node node, FieldMetadata fieldMetadata) {
      return new LabeledBoundField(binder, (Labeled) node, fieldMetadata);
   }

   public class LabeledBoundField extends AbstractBoundNodeMember implements BoundField {
      private final Labeled labeled;
      private final FieldMetadata fieldMetadata;

      public LabeledBoundField(JavaFXBinder binder, Labeled labeled, FieldMetadata fieldMetadata) {
         super(binder, labeled);
         this.labeled = labeled;
         this.fieldMetadata = fieldMetadata;
      }

      @Override
      public String getValue() {
         return labeled.getText();
      }

      @Override
      public void setValue(Object value) {
         labeled.setText(format(fieldMetadata, value));
      }

      public void updateForm() {
         getBinder().populateForm(fieldMetadata, getValue());
      }
   }
}
