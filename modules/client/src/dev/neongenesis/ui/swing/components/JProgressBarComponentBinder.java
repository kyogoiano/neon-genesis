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

import javax.swing.JProgressBar;

import org.apache.commons.beanutils.BeanUtilsBean;

import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.SwingBinder;

public class JProgressBarComponentBinder extends AbstractComponentBinder {
   public BoundField bind(SwingBinder binder, Component component,
      FieldMetadata fieldMetadata) {
      return new JProgressBarBoundField(binder, (JProgressBar) component,
         fieldMetadata);
   }

   public class JProgressBarBoundField extends AbstractBoundMember
         implements BoundField {
      private final JProgressBar component;
      private final FieldMetadata fieldMetadata;

      public JProgressBarBoundField(SwingBinder binder, JProgressBar component,
         FieldMetadata fieldMetadata) {
         super(binder, component);
         this.component = component;
         this.fieldMetadata = fieldMetadata;
      }

      protected JProgressBar getComponent() {
         return component;
      }

      protected FieldMetadata getFieldMetadata() {
         return fieldMetadata;
      }

      protected int toInt(Object value) {

          return BeanUtilsBean.getInstance()
                .getConvertUtils().lookup(Integer.TYPE).convert(Integer.TYPE,
                      value);
      }

      public void setValue(Object value) throws Exception {
         component.setValue(toInt(value));
      }

      public String getValue() {
         return format(fieldMetadata, component.getValue());
      }
   }
}
