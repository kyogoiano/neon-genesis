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

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import dev.neongenesis.text.FormatterRegistry;
import dev.neongenesis.ui.swing.SwingBinder;

public class FormatterTableCellRenderer extends DefaultTableCellRenderer {
   protected SwingBinder getBinder(JTable table) {
      return (SwingBinder) table.getClientProperty(SwingBinder.BINDER_KEY);
   }

   protected String getColumnIdentifier(JTable table, int index) {
      TableColumn column = table.getColumnModel().getColumn(index);
      String[] names = (String[]) table.getClientProperty(SwingBinder.COLUMN_NAMES);

      if (names != null && names.length > column.getModelIndex()) {
         return names[column.getModelIndex()];
      }

      return (String) column.getIdentifier();
   }

   protected Object format(JTable table, Object value, int column) {
      final SwingBinder binder = getBinder(table);
      if (binder == null) {
         return format(value);
      }

      final String property = getColumnIdentifier(table, column);
      boolean isVirtual = binder.isVirtual(table, property);

      return binder.format(binder.getName(table), property, value, isVirtual);
   }

   protected Object format(Object value) {
      return FormatterRegistry.getInstance().format(value);
   }

   public Component getTableCellRendererComponent(JTable table, Object value,
         boolean isSelected, boolean hasFocus, int row, int column) {
      return super.getTableCellRendererComponent(table, format(table, value,
            column), isSelected, hasFocus, row, column);
   }
}
