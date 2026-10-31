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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import org.apache.commons.beanutils.BeanUtilsBean;

import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.SwingBinder;

public class JToggleButtonComponentBinder extends AbstractComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new JToggleButtonBoundField(binder, (JToggleButton) component,
         fieldMetadata);
   }

   public class JToggleButtonBoundField extends AbstractBoundMember
         implements BoundField {
      private final JToggleButton component;
      private final FieldMetadata fieldMetadata;
      private final ActionListener listener;

      public JToggleButtonBoundField(SwingBinder binder,
         JToggleButton component, FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;

         this.component.addActionListener(listener = createActionListener());
      }

      protected JToggleButton getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected ActionListener getListener() {
         return listener;
      }

      protected ActionListener createActionListener() {
         return new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               getBinder().populateForm(getFieldMetadata(), getCurrentValue());
            }
         };
      }

      protected Object getCurrentValue() {
         return Boolean.valueOf(component.isSelected());
      }

      public String getValue() {
         return format(fieldMetadata, getCurrentValue());
      }

      protected boolean toBoolean(Object value) throws Exception {
         Boolean b = (Boolean) BeanUtilsBean.getInstance().getConvertUtils()
               .lookup(Boolean.TYPE).convert(Boolean.TYPE, value);

         return b.booleanValue();
      }

      public void setValue(Object value) throws Exception {
         deactivateListeners();

         try {
            component.setSelected(toBoolean(value));
         } finally {
            reactivateListeners();
         }
      }

      protected void deactivateListeners() {
         if (listener != null) {
            component.removeActionListener(listener);
         }
      }

      protected void reactivateListeners() {
         if (listener != null) {
            component.addActionListener(listener);
         }
      }

      public void unbind() {
         if (listener != null) {
            component.removeActionListener(listener);
         }
      }
   }
}
