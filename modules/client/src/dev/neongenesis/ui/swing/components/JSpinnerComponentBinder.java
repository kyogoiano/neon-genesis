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

import java.awt.Component;

import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.SwingBinder;

public class JSpinnerComponentBinder extends AbstractComponentBinder {
   private final boolean useFormatter;

   public JSpinnerComponentBinder() {
      this(false);
   }

   public JSpinnerComponentBinder(boolean useFormatter) {
      this.useFormatter = useFormatter;
   }

   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new JSpinnerBoundField(binder, (JSpinner) component,
         fieldMetadata, useFormatter);
   }

   public class JSpinnerBoundField extends AbstractBoundMember
         implements BoundField {
      private final JSpinner component;
      private final FieldMetadata fieldMetadata;
      private final boolean useFormatter;
      private final ChangeListener listener;

      public JSpinnerBoundField(SwingBinder binder, JSpinner component,
         FieldMetadata fieldMetadata, boolean useFormatter) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
         this.useFormatter = useFormatter;
         component.addChangeListener(listener = createChangeListener());
      }

      protected JSpinner getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      public boolean isUseFormatter() {
         return useFormatter;
      }

      protected ChangeListener getListener() {
         return listener;
      }

      protected ChangeListener createChangeListener() {
         return new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
               getBinder().populateForm(getFieldMetadata(), getCurrentValue());
            }
         };
      }

      protected Object getCurrentValue() {
         return component.getValue();
      }

      public String getValue() {
         return format(fieldMetadata, getCurrentValue());
      }

      public void setValue(Object value) throws Exception {
         deactivateListeners();

         try {
            component.setValue(useFormatter ? getBinder().getFormatter(
                  fieldMetadata).format(value) : value);
         } finally {
            reactivateListeners();
         }
      }

      protected void deactivateListeners() {
         if (listener != null) {
            component.removeChangeListener(listener);
         }
      }

      protected void reactivateListeners() {
         if (listener != null) {
            component.addChangeListener(listener);
         }
      }

      public void unbind() {
         if (listener != null) {
            component.removeChangeListener(listener);
         }
      }
   }
}
