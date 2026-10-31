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
import javafx.scene.control.ProgressIndicator;

/**
 * Binds an OpenJFX {@link ProgressIndicator} (including {@link javafx.scene.control.ProgressBar})
 * to numeric form fields.
 */
public class ProgressBarNodeBinder extends AbstractNodeBinder {

   @Override
   public BoundField bind(JavaFXBinder binder, Node node, FieldMetadata fieldMetadata) {
      return new ProgressBarBoundField(binder, (ProgressIndicator) node, fieldMetadata);
   }

   public class ProgressBarBoundField extends AbstractBoundNodeMember implements BoundField {
      private final ProgressIndicator indicator;
      private final FieldMetadata fieldMetadata;

      public ProgressBarBoundField(JavaFXBinder binder, ProgressIndicator indicator,
            FieldMetadata fieldMetadata) {
         super(binder, indicator);
         this.indicator = indicator;
         this.fieldMetadata = fieldMetadata;
      }

      @Override
      public String getValue() {
         return Double.toString(indicator.getProgress());
      }

      @Override
      public void setValue(Object value) {
         Double d = (Double) getBinder().getConverter(fieldMetadata).convert(Double.class, value);
         indicator.setProgress(d != null ? d.doubleValue() : 0.0);
      }

      public void updateForm() {
         getBinder().populateForm(fieldMetadata, Double.valueOf(indicator.getProgress()));
      }
   }
}
