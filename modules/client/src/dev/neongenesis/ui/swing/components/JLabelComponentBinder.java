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
package dev.neongenesis.ui.swing.components;

import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.SwingBinder;

import java.awt.Component;

import javax.swing.JLabel;

public class JLabelComponentBinder extends AbstractComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new JLabelComponentBoundField(binder, (JLabel) component,
         fieldMetadata);
   }

   public class JLabelComponentBoundField extends AbstractBoundMember
         implements BoundField {
      private final JLabel component;
      private final FieldMetadata fieldMetadata;

      public JLabelComponentBoundField(SwingBinder binder, JLabel component,
         FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
      }

      protected JLabel getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      public void setValue(Object value) {
         component.setText(getBinder().getFormatter(fieldMetadata).format(value));
      }

      public String getValue() {
         return component.getText();
      }
   }
}
