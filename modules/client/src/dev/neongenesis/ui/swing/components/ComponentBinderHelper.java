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
import java.lang.reflect.InvocationTargetException;
import javax.swing.JComponent;
import dev.neongenesis.ui.swing.SwingBinder;
import dev.neongenesis.util.Bundle;
import org.apache.commons.beanutils.PropertyUtils;

public class ComponentBinderHelper {
   private ComponentBinderHelper() {
   }

   public static String format(final SwingBinder binder, JComponent component, 
         Object value) {
      String valueProperty = (String) component
            .getClientProperty(SwingBinder.VALUE_PROPERTY);

      if (value == null) {
         String blankLabel = (String) component
               .getClientProperty(SwingBinder.BLANK_LABEL_PROPERTY);
         return (blankLabel == null) ? "" : blankLabel; // NOI18N
      } else if (value instanceof String) {
         return (String) value;
      } else if (valueProperty == null) {
         return binder.format(binder.getName(component), null, value, 
               binder.isVirtual(component));
      }

      boolean isVirtual = binder.isVirtual(component, valueProperty);

      return binder.format(binder.getName(component), valueProperty, 
            isVirtual ? value : getValue(binder, component, value, 
            valueProperty), isVirtual);
   }

   private static Object getValue(SwingBinder binder, Component component, 
         Object bean, String propertyName) {
      try {
         return PropertyUtils.getProperty(bean, propertyName);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e);
      } catch (NoSuchMethodException e) {
         IllegalArgumentException iae = new IllegalArgumentException(
               Bundle.getMessage(ComponentBinderHelper.class,
               "THE_COMPONENT_NAMED_X_WAS_EXPECTED_Y_TO_HAVE_A_PROPERTY_NAMED_Z_AT_BEAN_T", // NOI18N
               new Object[] {binder.getName(component),
                  bean.getClass().getName(), propertyName, bean}));
         iae.initCause(e);
         throw iae;
      }
   }

   public static String getKey(SwingBinder binder, JComponent component, 
         final String name, final Object value) throws Exception {
      String keyPropertyName = (String) component.getClientProperty(
            SwingBinder.KEY_PROPERTY);

      if (keyPropertyName != null) {
         Object o = (value == null) ? null : getValue(binder, component, value, 
               keyPropertyName);

         return check(binder.format(name, keyPropertyName, o), binder, name, 
               value);
      }

      return check(format(binder, component, value), binder, name, value);
   }
   
   private static String check(String formattedKey, SwingBinder binder, 
         String name, Object value) {
      if (formattedKey == null) {
         throw new IllegalStateException(Bundle.getMessage(
               ComponentBinderHelper.class,
               "KEY_PRODUCED_FOR_X_IN_Y_USING_VALUE_Z_WAS_NULL", new Object[] { // NOI18N
                  name, binder.getForm(), value}));
      }
      
      return formattedKey;
   }
}