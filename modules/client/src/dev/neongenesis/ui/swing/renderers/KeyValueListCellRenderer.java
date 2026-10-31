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
package dev.neongenesis.ui.swing.renderers;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

import dev.neongenesis.text.FormatterRegistry;
import dev.neongenesis.ui.swing.SwingBinder;
import dev.neongenesis.ui.swing.components.ComponentBinderHelper;
import dev.neongenesis.util.Bundle;

public class KeyValueListCellRenderer extends DefaultListCellRenderer {
   private final JComponent component;

   public KeyValueListCellRenderer(JComponent component) {
      if (component == null) {
         throw new IllegalArgumentException(Bundle.getMessage(
               KeyValueListCellRenderer.class, "COMPONENT_MUST_NOT_BE_NULL")); // NOI18N
      }
      this.component = component;
   }

   protected SwingBinder getBinder() {
      return (SwingBinder) component.getClientProperty(SwingBinder.BINDER_KEY);
   }

   protected String format(Object value) {
      final SwingBinder binder = getBinder();

      if (binder == null) {
         value = FormatterRegistry.getInstance().format(value);
      } else {
         value = ComponentBinderHelper.format(binder, component, value);
      }
      return (value == null || value.equals("") ? " " : value.toString());
   }

   public Component getListCellRendererComponent(JList list, Object value,
         int index, boolean isSelected, boolean cellHasFocus) {
      return super.getListCellRendererComponent(list, format(value), index,
            isSelected, cellHasFocus);
   }
}
