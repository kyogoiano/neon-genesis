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
package dev.neongenesis.ui.swing.factory;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import dev.neongenesis.ui.swing.SwingBinder;
import dev.neongenesis.ui.swing.renderers.KeyValueListCellRenderer;
import dev.neongenesis.util.Bundle;

public class SwingFactory {
   public static JComboBox createComboBox() {
      return createComboBox((String) null, (String) null, (String) null);
   }

   public static JComboBox createComboBox(String keyProperty) {
      return createComboBox(keyProperty, (String) null, (String) null);
   }

   public static JComboBox createComboBox(String keyProperty,
         String valueProperty) {
      return createComboBox(keyProperty, valueProperty, (String) null);
   }

   public static JComboBox createComboBox(String keyProperty,
         String valueProperty, String blankLabelProperty) {
      JComboBox combobox = new JComboBox();
      combobox.putClientProperty(SwingBinder.KEY_PROPERTY, keyProperty);
      combobox.putClientProperty(SwingBinder.VALUE_PROPERTY, valueProperty);

      if (blankLabelProperty != null) {
         combobox.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
         combobox.putClientProperty(SwingBinder.BLANK_LABEL_PROPERTY,
               blankLabelProperty);
      }

      combobox.setRenderer(new KeyValueListCellRenderer(combobox));

      return combobox;
   }

   public static JComboBox createComboBox(SwingBinder binder, String name) {
      return createComboBox(binder, name, (String) null, (String) null);
   }

   public static JComboBox createComboBox(SwingBinder binder, String name,
         String keyProperty) {
      return createComboBox(binder, name, keyProperty, (String) null);
   }

   public static JComboBox createComboBox(SwingBinder binder, String name,
         String keyProperty, String valueProperty) {
      return createComboBox(binder, name, keyProperty, valueProperty,
            (String) null);
   }

   public static JComboBox createComboBox(SwingBinder binder, String name,
         String keyProperty, String valueProperty, String blankLabelProperty) {
      JComboBox combobox = createComboBox(keyProperty, valueProperty,
            blankLabelProperty);
      combobox.setName(name);

      binder.getLookupStrategy().register(name, combobox);

      return combobox;
   }

   public static JList createList() {
      return createList((String) null, (String) null, (String) null);
   }

   public static JList createList(String keyProperty) {
      return createList(keyProperty, (String) null, (String) null);
   }

   public static JList createList(String keyProperty, String valueProperty) {
      return createList(keyProperty, keyProperty, (String) null);
   }

   public static JList createList(String keyProperty, String valueProperty,
         String blankLabelProperty) {
      JList list = new JList();
      list.putClientProperty(SwingBinder.KEY_PROPERTY, keyProperty);
      list.putClientProperty(SwingBinder.VALUE_PROPERTY, valueProperty);

      if (blankLabelProperty != null) {
         list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
         list.putClientProperty(SwingBinder.BLANK_LABEL_PROPERTY,
               blankLabelProperty);
      }

      return list;
   }

   public static JList createList(SwingBinder binder, String name) {
      return createList(binder, name, (String) null, (String) null,
            (String) null);
   }

   public static JList createList(SwingBinder binder, String name,
         String keyProperty) {
      return createList(binder, name, keyProperty, (String) null, (String) null);
   }

   public static JList createList(SwingBinder binder, String name,
         String keyProperty, String valueProperty) {
      return createList(binder, name, keyProperty, valueProperty, (String) null);
   }

   public static JList createList(SwingBinder binder, String name,
         String keyProperty, String valueProperty, String blankLabelProperty) {
      JList list = createList(keyProperty, valueProperty, blankLabelProperty);
      list.setName(name);
      list.setCellRenderer(new KeyValueListCellRenderer(list));

      binder.getLookupStrategy().register(name, list);

      return list;
   }

   public static JTable createTable(String[] columnIdentifiers,
         String[] columnDisplayName) {
      return createTable(columnIdentifiers, columnDisplayName, (int[]) null,
            (TableCellRenderer[]) null);
   }

   public static JTable createTable(String[] columnIdentifiers,
         String[] columnDisplayName, int[] preferredWidth) {
      return createTable(columnIdentifiers, columnDisplayName, preferredWidth,
            (TableCellRenderer[]) null);
   }

   public static JTable createTable(String[] columnIdentifiers,
         String[] columnDisplayName, int[] preferredWidth,
         TableCellRenderer[] renderers) {
      if (columnIdentifiers.length != columnDisplayName.length) {
         throw new IllegalArgumentException(
               Bundle.getMessage(SwingFactory.class,
               "COLUMN_IDENTIFIERS_AND_NAMES_MUST_HAVE_SAME_LENGTH")); // NOI18N
      }

      if (preferredWidth != null
            && preferredWidth.length != columnIdentifiers.length) {
         throw new IllegalArgumentException(
               Bundle.getMessage(SwingFactory.class,
               "COLUMN_IDENTIFIERS_AND_PREFERRED_WIDTHS_MUST_HAVE_SAME_LENGTH")); // NOI18N
      }

      if (renderers != null && renderers.length != columnIdentifiers.length) {
         throw new IllegalArgumentException(
               Bundle.getMessage(SwingFactory.class,
               "COLUMN_IDENTIFIERS_AND_RENDERERS_MUST_HAVE_SAME_LENGTH")); // NOI18N
      }

      TableColumnModel columnModel = new DefaultTableColumnModel();
      TableModel model = new DefaultTableModel(columnDisplayName, 0) {
         public boolean isCellEditable(int row, int column) {
            return false;
         }
      };

      for (int i = 0; i < columnIdentifiers.length; i++) {
         TableColumn column = new TableColumn(i);
         //column.setIdentifier(columnIdentifiers[i]);
         column.setHeaderValue(columnDisplayName[i]);

         if (preferredWidth != null && preferredWidth[i] > 0) {
            column.setPreferredWidth(preferredWidth[i]);
         }

         if (renderers != null && renderers[i] != null) {
            column.setCellRenderer(renderers[i]);
         }

         columnModel.addColumn(column);
      }

      JTable table = new JTable(model, columnModel);
      table.putClientProperty(SwingBinder.COLUMN_NAMES, columnIdentifiers);

      return table;
   }

   public static JTable createTable(SwingBinder binder, String name,
         String[] columnIdentifiers, String[] columnDisplayName) {
      return createTable(binder, name, columnIdentifiers, columnDisplayName,
            (int[]) null, (TableCellRenderer[]) null);
   }

   public static JTable createTable(SwingBinder binder, String name,
         String[] columnIdentifiers, String[] columnDisplayName,
         int[] preferredWidth) {
      return createTable(binder, name, columnIdentifiers, columnDisplayName,
            preferredWidth, (TableCellRenderer[]) null);
   }

   public static JTable createTable(SwingBinder binder, String name,
         String[] columnIdentifiers, String[] columnDisplayName,
         int[] preferredWidth, TableCellRenderer[] renderers) {
      JTable table = createTable(columnIdentifiers, columnDisplayName,
            preferredWidth, renderers);
      table.setName(name);

      binder.getLookupStrategy().register(name, table);

      return table;
   }
}
