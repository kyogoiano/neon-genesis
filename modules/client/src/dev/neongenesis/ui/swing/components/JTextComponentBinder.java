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
import java.awt.EventQueue;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.binding.BindingStrategy;

import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.SwingBinder;

public class JTextComponentBinder extends AbstractComponentBinder {
   private final boolean trim;

   public JTextComponentBinder() {
      this(true);
   }

   public JTextComponentBinder(boolean trim) {
      this.trim = trim;
   }
   
   protected boolean isTrim() {
      return trim;
   }

   public BoundField bind(SwingBinder binder, Component component,
         FieldMetadata fieldMetadata) {
      return new JTextComponentBoundField(binder, (JTextComponent) component,
         fieldMetadata);
   }

   public static abstract class JTextComponentBindingStrategy 
         implements BindingStrategy {
      public void addListener(Object widget) {
         addListener((JTextComponent)widget);
      }

      protected abstract void addListener(JTextComponent component);

      public void removeListener(Object widget) {
         removeListener((JTextComponent)widget);
      }

      protected abstract void removeListener(JTextComponent component);
   }

   public class JTextComponentBoundField extends AbstractBoundMember
         implements BoundField {
      public class FocusBindingStrategy extends JTextComponentBindingStrategy {
         private FocusListener listener;

         protected void addListener(JTextComponent widget) {
            component.addFocusListener(listener = createFocusListener());
         }

         protected FocusListener createFocusListener() {
            return new FocusAdapter() {
               public void focusLost(FocusEvent event) {
                  updateForm();
               }
            };
         }

         public void setValue(Object value) {
            updateComponentValue(value);
         }

         public void removeListener(JTextComponent component) {
            if (listener != null) {
               component.removeFocusListener(listener);
            }
         }
      }

      public class DocumentBindingStrategy extends JTextComponentBindingStrategy {
         private DocumentListener listener;
         private boolean processing;

         public void addListener(JTextComponent component) {
            component.getDocument().addDocumentListener(
                  listener = createDocumentListener());
         }

         protected DocumentListener createDocumentListener() {
            return new DocumentListener() {
               public void changedUpdate(DocumentEvent e) {
                  update();
               }

               public void insertUpdate(DocumentEvent e) {
                  update();
               }

               public void removeUpdate(DocumentEvent e) {
                  update();
               }

               private void update() {
                  processing = true;

                  try {
                     updateForm();
                  } finally {
                     processing = false;
                  }
               }
            };
         }

         public void setValue(final Object value) {
            final Runnable updateValue = new Runnable() {
               public void run() {
                  updateComponentValue(value);
               }
            };

            if (!processing) {
               updateValue.run();
            } else {
               if (!fieldMetadata.getEqualityComparator().equals(
                     formatValue(value), getValue())) {
                  EventQueue.invokeLater(updateValue);
               }
            }
         }

         public void removeListener(JTextComponent component) {
            if (listener != null) {
               component.getDocument().removeDocumentListener(listener);
            }
         }
      }

      private final JTextComponent component;
      private final FieldMetadata fieldMetadata;
      private final BindingStrategy strategy;

      public JTextComponentBoundField(SwingBinder binder,
            JTextComponent component, FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
         this.strategy = createBindingStrategy();

         strategy.addListener(component);
      }

      protected JTextComponent getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected BindingStrategy createBindingStrategy() {
         return AbstractBinder.BINDING_STRATEGY_AS_YOU_TYPE.equals(
               getBinder().getBindingStrategy((String)component
               .getClientProperty(AbstractBinder.BINDING_STRATEGY_PROPERTY))) ? 
               (BindingStrategy)new DocumentBindingStrategy() : 
               new FocusBindingStrategy();
      }

      public void updateForm() {
         getBinder().populateForm(getFieldMetadata(), getValue());
      }

      public String getValue() {
         return isTrim() ? component.getText().trim() : component.getText();
      }

      public String formatValue(Object value) {
         return getBinder().getFormatter(fieldMetadata).format(value);
      }

      public void updateComponentValue(Object value) {
         component.setText(formatValue(value));
      }

      public void setValue(Object value) {
         strategy.setValue(value);
      }

      public void unbind() {
         strategy.removeListener(component);
      }
   }
}
