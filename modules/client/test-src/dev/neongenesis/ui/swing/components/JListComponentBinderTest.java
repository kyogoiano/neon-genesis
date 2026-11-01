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

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockBean;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.reflection.MethodEntry;
import dev.neongenesis.ui.binding.BoundDataProvider;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;
import dev.neongenesis.ui.swing.SwingBinder;

public class JListComponentBinderTest extends GenesisTestCase {
   private JList list;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundDataProvider boundDataProvider;
   private BoundField boundField;
   private MockForm form;
   private DataProviderMetadata dataMeta;

   public JListComponentBinderTest() {
      super("JList Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      list = SwingUtils.newList();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      componentBinder = binder.getWidgetBinder(list);
      dataMeta.setResetSelection(true);
   }

   public void testSelectIndexes() {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      assertTrue(componentBinder instanceof JListComponentBinder);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, list, dataMeta));

      list.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2 }, indexes));

      list.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      list.setSelectedIndex(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      list.clearSelection();
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {2, 3});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2, 3 }, indexes));

      list.setSelectedIndices(new int[] {});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {-1});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testSelectIndexesWithBlank() {
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, list, dataMeta));

      list.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      list.setSelectedIndex(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      list.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      list.setSelectedIndex(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      list.setSelectedIndices(new int[] {2, 3});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1, 2 }, indexes));
      
      list.setSelectedIndices(new int[] {0});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));

      list.setSelectedIndices(new int[] {-1});
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] {}, indexes));
   }

   public void testUpdateIndexes() {
      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundDataProvider = componentBinder.bind(binder, list,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, list.getSelectedIndex());
      
      boundDataProvider.updateIndexes(new int[] { 2, 4 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {-1});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
   }

   public void testUpdateIndexesWithBlank() {
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundDataProvider = componentBinder.bind(binder, list,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(3, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(2, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, list.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, list.getSelectedIndex());
      
      boundDataProvider.updateIndexes(new int[] { 1, 3 });
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
      
      boundDataProvider.updateIndexes(new int[] {-1});
      assertTrue(Arrays.equals(new int[] {}, list.getSelectedIndices()));
   }

   public void testUpdateList() throws Exception {
      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, list, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = list.getModel().getSize();
      assertEquals(-1, list.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      list.setSelectedIndices(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertEquals(0, list.getSelectedIndices().length);
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      dataMeta.setResetSelection(false);
      list.setSelectedIndices(new int[] {0, 1, 2});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0, 1, 2}, list.getSelectedIndices()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectedIndices()));
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertEquals(count, newList.length);
   }

   public void testUpdateListWithBlank() throws Exception {
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, list, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      list.setSelectedIndices(new int[] {1, 2, 3});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {0}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      dataMeta.setResetSelection(false);
      list.setSelectedIndices(new int[] {1, 2, 3});
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {1, 2, 3}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertTrue(Arrays.equals(new int[] {1}, list.getSelectedIndices()));
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(list.getModel().getElementAt(i + 1), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = list.getModel().getSize();
      assertEquals(count, newList.length + 1);
   }

   public void testSetValue() throws Exception {
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(3, list.getSelectedIndex());

      value = model.getElementAt(0);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(0, list.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueWithoutKey() throws Exception {
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object value = new String(((MockBean)model.getElementAt(3)).getKey());
      boundField.setValue(value);
      assertEquals(value, ((MockBean)list.getSelectedValue()).getKey());
      assertEquals(3, list.getSelectedIndex());

      value = new String(((MockBean)model.getElementAt(0)).getKey());
      boundField.setValue(value);
      assertEquals(value, ((MockBean)list.getSelectedValue()).getKey());
      assertEquals(0, list.getSelectedIndex());

      value = new String("none");
      boundField.setValue(value);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueUsingString() throws Exception {
      JList list = SwingUtils.newStringList();
      MockSwingBinder binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      DataProviderMetadata dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      WidgetBinder componentBinder = binder.getWidgetBinder(list);
      dataMeta.setResetSelection(true);

      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(3, list.getSelectedIndex());

      value = new String(model.getElementAt(0).toString());
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(0, list.getSelectedIndex());

      value = new String("none");
      boundField.setValue(value);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueWithBlank() throws Exception {
      list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(3, list.getSelectedIndex());

      value = model.getElementAt(1);
      boundField.setValue(value);
      assertEquals(value, list.getSelectedValue());
      assertEquals(1, list.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueMultiSelection() throws Exception {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object[] values = new Object[] { model.getElementAt(2),
            model.getElementAt(4) };
      boundField.setValue(values);
      assertEquals(Arrays.asList(values), list.getSelectedValuesList());
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));

      values = new Object[] { model.getElementAt(0) };
      boundField.setValue(values);
      assertEquals(values[0], list.getSelectedValue());
      assertEquals(0, list.getSelectedIndex());

      values = new Object[] { new MockBean("none", "None") };
      boundField.setValue(values);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }

   public void testSetValueMultiSelectionWithBlank() throws Exception {
      list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      list.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      list.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, list, (ActionMetadata) null));
      assertNull(componentBinder.bind(binder, list, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            list, dataMeta));

      DefaultListModel model = (DefaultListModel) list.getModel();

      Object[] values = new Object[] { model.getElementAt(2),
            model.getElementAt(4) };
      boundField.setValue(values);
      assertEquals(Arrays.asList(values), list.getSelectedValuesList());
      assertTrue(Arrays.equals(new int[] { 2, 4 }, list.getSelectedIndices()));

      values = new Object[] { model.getElementAt(1) };
      boundField.setValue(values);
      assertEquals(values[0], list.getSelectedValue());
      assertEquals(1, list.getSelectedIndex());

      values = new Object[] { new MockBean("none", "None") };
      boundField.setValue(values);
      assertNull(list.getSelectedValue());
      assertEquals(-1, list.getSelectedIndex());
   }
}
