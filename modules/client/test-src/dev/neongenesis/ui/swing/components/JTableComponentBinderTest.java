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

import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JTable;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockBean;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.reflection.MethodEntry;
import dev.neongenesis.ui.binding.BoundDataProvider;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;

import org.apache.commons.beanutils.PropertyUtils;

public class JTableComponentBinderTest extends GenesisTestCase {
   private JTable table;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundDataProvider boundDataProvider;
   private MockForm form;
   private DataProviderMetadata dataMeta;

   public JTableComponentBinderTest() {
      super("JTable Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      table = SwingUtils.newTable();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      componentBinder = binder.getWidgetBinder(table);
      dataMeta.setResetSelection(true);
   }

   public void testSelectIndexes() {
      assertTrue(componentBinder instanceof JTableComponentBinder);

      assertNull(componentBinder.bind(binder, table, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, table, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, table, dataMeta));

      table.getSelectionModel().clearSelection();
      table.getSelectionModel().addSelectionInterval(2, 2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2 }, indexes));

      table.getSelectionModel().clearSelection();
      table.getSelectionModel().addSelectionInterval(0, 0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      table.getSelectionModel().clearSelection();
      table.getSelectionModel().addSelectionInterval(1, 1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      table.getSelectionModel().clearSelection();
      table.getSelectionModel().addSelectionInterval(-1, -1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      table.getSelectionModel().clearSelection();
      table.getSelectionModel().addSelectionInterval(2, 3);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2, 3 }, indexes));

      table.getSelectionModel().clearSelection();
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testUpdateIndexes() {
      assertNull(componentBinder.bind(binder, table, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, table, (FieldMetadata) null));
      assertNotNull(boundDataProvider = componentBinder.bind(binder, table,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, table.getSelectedRow());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, table.getSelectedRow());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, table.getSelectedRow());
      
      boundDataProvider.updateIndexes(new int[] { 2, 4 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, table.getSelectedRows()));
      
      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, table.getSelectedRows()));
      
      boundDataProvider.updateIndexes(new int[] {-1});
      assertTrue(Arrays.equals(new int[] {}, table.getSelectedRows()));
   }

   public void testUpdateList() throws Exception {
      assertNull(componentBinder.bind(binder, table, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, table, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, table, dataMeta);
      assertNotNull(boundDataProvider);

      Object[] newList = new Object[] { new MockBean("newOne", "NewOne"),
            new MockBean("newTwo", "NewTwo"), new MockBean("newThree", "NewThree") };

      boundDataProvider.updateList(Arrays.asList(newList));
      int count = table.getModel().getRowCount();
      assertEquals(0, table.getSelectedRows().length);
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 3; j++) {
            assertEquals(table.getModel().getValueAt(i, j), j == 2 ? null : PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      table.getSelectionModel().addSelectionInterval(0, 2);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getModel().getRowCount();
      assertEquals(0, table.getSelectedRows().length);
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 3; j++) {
            assertEquals(table.getModel().getValueAt(i, j), j == 2 ? null : PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      dataMeta.setResetSelection(false);
      table.getSelectionModel().addSelectionInterval(0, 2);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getModel().getRowCount();
      assertTrue(Arrays.equals(new int[] {0, 1, 2}, table.getSelectedRows()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 3; j++) {
            assertEquals(table.getModel().getValueAt(i, j), j == 2 ? null : PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      newList = new Object[] { new MockBean("other", "Other", (MockBean)newList[0]) };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getModel().getRowCount();
      assertTrue(Arrays.equals(new int[] {0}, table.getSelectedRows()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         for (int j = 0; j < 3; j++) {
            assertEquals(table.getModel().getValueAt(i, j), j == 2 ? "newOne" : PropertyUtils
                  .getProperty(newList[i], j == 0 ? "key" : "value"));
         }
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = table.getModel().getRowCount();
      assertEquals(count, newList.length);
   }

}
