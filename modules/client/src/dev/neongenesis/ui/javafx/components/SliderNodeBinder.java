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
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Slider;

/**
 * Binds an OpenJFX {@link Slider} to numeric form fields.
 */
public class SliderNodeBinder extends AbstractNodeBinder {

   @Override
   public BoundField bind(JavaFXBinder binder, Node node, FieldMetadata fieldMetadata) {
      return new SliderBoundField(binder, (Slider) node, fieldMetadata);
   }

   public class SliderBoundField extends AbstractBoundNodeMember implements BoundField {
      private final Slider slider;
      private final FieldMetadata fieldMetadata;
      private final ChangeListener<Number> listener;
      private boolean updating;

      public SliderBoundField(JavaFXBinder binder, Slider slider, FieldMetadata fieldMetadata) {
         super(binder, slider);
         this.slider = slider;
         this.fieldMetadata = fieldMetadata;
         this.listener = (obs, oldVal, newVal) -> {
            if (!updating) {
               updateForm();
            }
         };
         this.slider.valueProperty().addListener(this.listener);
      }

      @Override
      public String getValue() {
         return Double.toString(slider.getValue());
      }

      @Override
      public void setValue(Object value) {
         updating = true;
         try {
            Double d = (Double) getBinder().getConverter(fieldMetadata).convert(Double.class, value);
            slider.setValue(d != null ? d.doubleValue() : 0.0);
         } finally {
            updating = false;
         }
      }

      public void updateForm() {
         getBinder().populateForm(fieldMetadata, Double.valueOf(slider.getValue()));
      }

      @Override
      public void unbind() {
         slider.valueProperty().removeListener(listener);
      }
   }
}
