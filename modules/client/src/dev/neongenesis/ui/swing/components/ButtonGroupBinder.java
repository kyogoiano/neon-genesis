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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;

import dev.neongenesis.helpers.EnumHelper;
import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.GroupBinder;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.SwingBinder;

import dev.neongenesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ButtonGroupBinder implements GroupBinder {
   private static final Log log = LogFactory.getLog(ButtonGroupBinder.class);

   private final boolean useActionCommandAsSelectedValue;

   public ButtonGroupBinder() {
      this(false);
   }

   public ButtonGroupBinder(boolean useActionCommandAsSelectedValue) {
      this.useActionCommandAsSelectedValue = useActionCommandAsSelectedValue;
   }

   public BoundField bind(AbstractBinder binder, Object group,
      FieldMetadata fieldMetadata) {
      return new ButtonGroupBoundField(binder, (ButtonGroup) group,
         fieldMetadata);
   }

   public class ButtonGroupBoundField implements BoundField {
      private final AbstractBinder binder;
      private final ButtonGroup buttonGroup;
      private final FieldMetadata fieldMetadata;
      private final ActionListener listener;

      public ButtonGroupBoundField(AbstractBinder binder, ButtonGroup buttonGroup,
         FieldMetadata fieldMetadata) {
         this.binder = binder;
         this.buttonGroup = buttonGroup;
         this.fieldMetadata = fieldMetadata;
         addActionListener(listener = createActionListener());
      }

      protected AbstractBinder getBinder() {
         return binder;
      }

      protected ButtonGroup getButtonGroup() {
         return buttonGroup;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected String format(Object value) {
         return binder.format(getName(), fieldMetadata.getFieldName(), value, false);
      }

      protected void addActionListener(ActionListener listener) {
         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.addActionListener(listener);
         }
      }

      protected ActionListener createActionListener() {
         return new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               AbstractButton button = (AbstractButton) event.getSource();

               if (!button.isSelected()) {
                  return;
               }

               getBinder().populateForm(getFieldMetadata(), getValue(button));
            }
         };
      }

      protected Object getValue(AbstractButton button) {
         Object value;
         
         if (useActionCommandAsSelectedValue) {
            value = button.getActionCommand();
         } else {
            value = button.getClientProperty(
                  SwingBinder.BUTTON_GROUP_SELECTION_VALUE);
            
            if (value == null) {
               value = button.getName();
            }
         }

         if (value == null) {
            if (useActionCommandAsSelectedValue) {
               log.warn(
                     Bundle.getMessage(ButtonGroupBinder.class,
                     "USE_BUTTON_SETACTIONCOMMAND_SOMEVALUE_TO_DEFINE_ITS_VALUE", // NOI18N
                     button));
            } else {
               log.warn(
                     Bundle.getMessage(ButtonGroupBinder.class,
                     "USE_BUTTON_PUTCLIENTPROPERTY_SWINGBINDER_BUTTON_GROUP_SELECTION_VALUE_SOMEVALUE_OR_BUTTON_SETNAME_SOMEVALUE_TO_DEFINE_ITS_VALUE", // NOI18N
                     button));
            }
         }

         return value;
      }

      protected boolean equals(Object newValue, Object currentButtonValue) {
         if (newValue == null) {
            return currentButtonValue == null;
         }

         Class newClass = newValue.getClass();
         Class currentClass = currentButtonValue.getClass();

         if (newClass == String.class && currentClass != String.class) {
            currentButtonValue = EnumHelper.getInstance().isEnum(currentButtonValue) ?
                  EnumHelper.getInstance().getName(currentButtonValue) :
                  format(currentButtonValue);
         } else if (newClass != String.class && currentClass == String.class) {
            newValue = EnumHelper.getInstance().isEnum(newValue) ?
                  EnumHelper.getInstance().getName(newValue) :
                  format(newValue);
         }
               
         return newValue.equals(currentButtonValue);
      }

      public void setValue(Object value) throws Exception {
         if (useActionCommandAsSelectedValue && value != null) {
            value = getBinder().getFormatter(getFieldMetadata()).format(value);
         }

         Enumeration en = buttonGroup.getElements();
         while (en.hasMoreElements()) {
            AbstractButton button = (AbstractButton) en.nextElement();

            if (!equals(value, getValue(button))) {
               buttonGroup.setSelected(button.getModel(), false);
               continue;
            }

            buttonGroup.setSelected(button.getModel(), true);
            break;
         }
      }

      public String getValue() {
         Enumeration en = buttonGroup.getElements();
         while (en.hasMoreElements()) {
            AbstractButton button = (AbstractButton) en.nextElement();

            if (button.isSelected()) {
               return format(getValue(button));
            }
         }

         return null;
      }

      public void setEnabled(boolean enabled) {
         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.setEnabled(enabled);
         }
      }

      public void setVisible(boolean visible) {
         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.setVisible(visible);
         }
      }

      public String getName() {
         return fieldMetadata.getName();
      }

      public void unbind() {
         if (listener == null) {
            return;
         }

         Enumeration elements = buttonGroup.getElements();

         while (elements.hasMoreElements()) {
            AbstractButton button = (AbstractButton) elements.nextElement();
            button.removeActionListener(listener);
         }
      }
   }
}
