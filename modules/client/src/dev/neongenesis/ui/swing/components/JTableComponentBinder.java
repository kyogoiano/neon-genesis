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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import dev.neongenesis.ui.binding.BoundDataProvider;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.swing.SwingBinder;
import dev.neongenesis.ui.swing.components.table.JTableIndexResolver;
import dev.neongenesis.ui.swing.components.table.JTableIndexResolverRegistry;
import dev.neongenesis.ui.swing.renderers.FormatterTableCellRenderer;
import dev.neongenesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JTableComponentBinder extends AbstractComponentBinder {
   private static Log log = LogFactory.getLog(JTableComponentBinder.class);

   public BoundDataProvider bind(SwingBinder binder, Component component,
         DataProviderMetadata dataProviderMetadata) {
      JTableIndexResolver indexResolver = JTableIndexResolverRegistry.
            getInstance().
            get(component.getClass(), true);
      if (indexResolver == null) {
         log.error(Bundle.getMessage(JTableComponentBinder.class,
               "COULD_NOT_FIND_SUITABLE_JTABLEINDEXRESOLVER_IMPL_FOR_X_COMPONENT_Y", // NOI18N
               component.getClass(), binder.getName(component)));
         return null;
      }

      return new JTableComponentBoundDataProvider(binder, (JTable)component,
            dataProviderMetadata, indexResolver);
   }

   public class JTableComponentBoundDataProvider extends AbstractBoundMember
         implements BoundDataProvider {
      private final JTable component;
      private final DataProviderMetadata dataProviderMetadata;
      private final ListSelectionListener listener;
      private final TableCellRenderer renderer;
      private JTableIndexResolver indexResolver;

      public JTableComponentBoundDataProvider(SwingBinder binder,
            JTable component, DataProviderMetadata dataProviderMetadata,
            JTableIndexResolver indexResolver) {
         super(binder, component);
         this.component = component;
         this.dataProviderMetadata = dataProviderMetadata;
         this.indexResolver = indexResolver;
         this.component.getSelectionModel().addListSelectionListener(
               listener = createListSelectionListener());

         this.renderer = createCellRenderer();

         configureTableCellRenderer();
      }

      protected JTable getComponent() {
         return component;
      }

      protected DataProviderMetadata getDataProviderMetadata() {
         return dataProviderMetadata;
      }

      protected ListSelectionListener getListener() {
         return listener;
      }

      protected TableCellRenderer createCellRenderer() {
         return new FormatterTableCellRenderer();
      }

      protected void configureTableCellRenderer() {
         Enumeration en = component.getColumnModel().getColumns();

         while (en.hasMoreElements()) {
            TableColumn column = (TableColumn)en.nextElement();

            if (column.getCellRenderer() == null) {
               column.setCellRenderer(renderer);
            }
         }
      }

      protected ListSelectionListener createListSelectionListener() {
         return new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
               if (event.getValueIsAdjusting()) {
                  return;
               }

               getBinder().updateFormSelection(getDataProviderMetadata(),
                     getIndexes());
            }
         };
      }

      protected int[] getIndexes() {
         int[] indexes = component.getSelectedRows();

         if (!indexResolver.needsConversion(component)) {
            return indexes;
         }

         try {
            for (int i = 0; i < indexes.length; i++) {
               indexes[i] = indexResolver.convertRowIndexToModel(component,
                     indexes[i]);
            }
         } catch (IllegalArgumentException iae) {
            throw new RuntimeException(iae);
         } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
         } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
         }

         return indexes;
      }

      public void updateIndexes(int[] indexes) {
         fillViewIndexes(indexes);

         if (Arrays.equals(getIndexes(), indexes)) {
            return;
         }

         deactivateSelectionListener();

         try {
            ListSelectionModel sm = component.getSelectionModel();
            sm.clearSelection();

            for (int i = 0; i < indexes.length; i++) {
               if (indexes[i] == -1) {
                  continue;
               }

               sm.addSelectionInterval(indexes[i], indexes[i]);
            }
         } finally {
            reactivateSelectionListener();
         }
      }

      protected void fillViewIndexes(final int[] indexes) {
         if (!indexResolver.needsConversion(component)) {
            return;
         }

         try {
            for (int i = 0; i < indexes.length; i++) {
               indexes[i] = indexResolver.convertRowIndexToView(component,
                     indexes[i]);
            }
         } catch (IllegalArgumentException iae) {
            throw new RuntimeException(iae);
         } catch (InvocationTargetException ite) {
            throw new RuntimeException(ite.getCause());
         } catch (IllegalAccessException iae) {
            throw new RuntimeException(iae);
         }
      }

      public void updateList(List data) throws Exception {
         int[] selected = dataProviderMetadata.isResetSelection() ? new int[0]
               : getIndexes();

         createTableModelAdapter().setData(data);
         setSelectedIndexes(data.size(), selected);
      }

      protected void setSelectedIndexes(int listSize, int[] indexes) {
         fillViewIndexes(indexes);
         deactivateSelectionListener();

         try {
            component.clearSelection();
            for (int i = 0; i < indexes.length; i++) {
               if (indexes[i] >= listSize) {
                  continue;
               }

               component.getSelectionModel().addSelectionInterval(indexes[i],
                     indexes[i]);
            }
         } finally {
            reactivateSelectionListener();
         }
      }

      protected void deactivateSelectionListener() {
         if (listener != null) {
            component.getSelectionModel().removeListSelectionListener(listener);
         }
      }

      protected void reactivateSelectionListener() {
         if (listener != null) {
            component.getSelectionModel().addListSelectionListener(listener);
         }
      }

      protected TableModelAdapter createTableModelAdapter() {
         return new TableModelAdapter() {
            public void setData(List data) throws Exception {
               DefaultTableModel model = (DefaultTableModel)component.getModel();

               int dataSize = data.size();
               if (model.getRowCount() != data.size()) {
                  model.setRowCount(dataSize);
               }

               int columnCount = component.getColumnModel().getColumnCount();
               if (model.getColumnCount() != columnCount) {
                  model.setColumnCount(columnCount);
               }

               int i = 0;
               TableColumn column;
               String propertyName;
               for (Iterator iter = data.iterator(); iter.hasNext(); i++) {
                  Object bean = iter.next();

                  for (int j = 0; j < model.getColumnCount(); j++) {
                     column = component.getColumnModel().getColumn(j);
                     int modelIndex = column.getModelIndex();

                     Object value = getBinder().isVirtual(
                           propertyName = getIdentifier(column)) ? bean
                           : getProperty(bean, propertyName);
                     model.setValueAt(value, i, modelIndex);
                  }
               }
            }
         };
      }

      protected String getIdentifier(TableColumn column) {
         String identifier = null;
         String[] names = (String[])component.getClientProperty(
               SwingBinder.COLUMN_NAMES);

         if (names != null && names.length > column.getModelIndex()) {
            identifier = names[column.getModelIndex()];

            if (identifier != null) {
               return identifier;
            }
         }

         identifier = (String)column.getIdentifier();
         if (identifier == null) {
            throw new IllegalArgumentException(Bundle.getMessage(
                  JTableComponentBinder.class,
                  "COLUMN_NUMBER_X_FROM_TABLE_Y_DOES_NOT_HAVE_AN_IDENTIFIER", // NOI18N
                    column.getModelIndex(), getBinder().getName(
                  getComponent())));
         }

         return identifier;
      }

      public void unbind() {
         if (listener != null) {
            component.getSelectionModel().removeListSelectionListener(listener);
         }

         if (renderer != null) {
            Enumeration en = component.getColumnModel().getColumns();

            while (en.hasMoreElements()) {
               TableColumn column = (TableColumn)en.nextElement();

               if (column.getCellRenderer() == renderer) {
                  column.setCellRenderer(null);
               }
            }
         }
      }
   }
}
