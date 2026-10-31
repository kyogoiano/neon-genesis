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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockBean;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.reflection.MethodEntry;
import dev.neongenesis.ui.binding.BoundDataProvider;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;
import dev.neongenesis.ui.swing.SwingBinder;

public class JComboBoxComponentBinderTest extends GenesisTestCase {
   private JComboBox combo;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundDataProvider boundDataProvider;
   private BoundField boundField;
   private MockForm form;
   private DataProviderMetadata dataMeta;

   public JComboBoxComponentBinderTest() {
      super("JComboBox Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      combo = SwingUtils.newCombo();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      componentBinder = binder.getWidgetBinder(combo);
      dataMeta.setResetSelection(true);
   }

   public void testSelectIndexes() {
      assertTrue(componentBinder instanceof JComboBoxComponentBinder);
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, combo, dataMeta));

      combo.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 2 }, indexes));

      combo.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      combo.setSelectedIndex(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testSelectIndexesWithBlank() {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(componentBinder.bind(binder, combo, dataMeta));

      combo.setSelectedIndex(2);
      int[] indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 1 }, indexes));

      combo.setSelectedIndex(1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[] { 0 }, indexes));

      combo.setSelectedIndex(0);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));

      combo.setSelectedIndex(-1);
      indexes = (int[]) binder
            .get("updateFormSelection(DataProviderMetadata,int[])");
      assertNotNull(indexes);
      assertTrue(Arrays.equals(new int[0], indexes));
   }

   public void testUpdateIndexes() {
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundDataProvider = componentBinder.bind(binder, combo,
            dataMeta));

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(2, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(1, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(0, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] {});
      assertEquals(-1, combo.getSelectedIndex());

      try {
         boundDataProvider.updateIndexes(new int[] {1, 2});
         fail("IllegalArgumentException should be thrown when length of array is greater than one");
      } catch (IllegalArgumentException e) {
         // expected
      }
   }

   public void testUpdateIndexesWithBlank() {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);

      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      boundDataProvider.updateIndexes(new int[] { 2 });
      assertEquals(3, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 1 });
      assertEquals(2, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { -1 });
      assertEquals(-1, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] { 0 });
      assertEquals(1, combo.getSelectedIndex());

      boundDataProvider.updateIndexes(new int[] {});
      assertEquals(-1, combo.getSelectedIndex());

      try {
         boundDataProvider.updateIndexes(new int[] {1, 2});
         fail("IllegalArgumentException should be thrown when length of array is greater than one");
      } catch (IllegalArgumentException e) {
         // expected
      }
   }

   public void testUpdateList() throws Exception {
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(-1, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      combo.setSelectedIndex(2);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(-1, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      dataMeta.setResetSelection(false);
      combo.setSelectedIndex(2);

      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(2, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(-1, combo.getSelectedIndex());
      assertEquals(count, newList.length);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length);
   }

   public void testUpdateListWithBlank() throws Exception {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));

      boundDataProvider = componentBinder.bind(binder, combo, dataMeta);
      assertNotNull(boundDataProvider);

      String[] newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      int count = combo.getItemCount();
      assertEquals(0, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      combo.setSelectedIndex(3);
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(0, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      dataMeta.setResetSelection(false);
      combo.setSelectedIndex(3);
      
      newList = new String[] { "newOne", "newTwo", "newThree" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(3, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      newList = new String[] { "other" };
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(0, combo.getSelectedIndex());
      assertEquals(count, newList.length + 1);
      for (int i = 0; i < newList.length; i++) {
         assertEquals(combo.getItemAt(i + 1), newList[i]);
      }

      newList = new String[0];
      boundDataProvider.updateList(Arrays.asList(newList));
      count = combo.getItemCount();
      assertEquals(count, newList.length + 1);
   }

   public void testSetValue() throws Exception {
      combo.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            combo, dataMeta));

      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(0);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(0, combo.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());
   }

   public void testSetValueWithoutKey() throws Exception {
      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            combo, dataMeta));

      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      Object value = ((MockBean)model.getElementAt(3)).getKey();
      boundField.setValue(value);
      assertEquals(value, ((MockBean)combo.getSelectedItem()).getKey());
      assertEquals(3, combo.getSelectedIndex());

      value = ((MockBean)model.getElementAt(0)).getKey();
      boundField.setValue(value);
      assertEquals(value, ((MockBean)combo.getSelectedItem()).getKey());
      assertEquals(0, combo.getSelectedIndex());

      value = "none";
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());
   }

   public void testSetValueUsingString() throws Exception {
      JComboBox combo = SwingUtils.newStringCombo();
      MockSwingBinder binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      WidgetBinder componentBinder = binder.getWidgetBinder(combo);

      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            combo, dataMeta));

      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(new String(value.toString()));
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(0);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(0, combo.getSelectedIndex());

      value = model.getElementAt(3);
      boundField.setValue(new String(value.toString()));
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(0);
      boundField.setValue(new String(value.toString()));
      assertEquals(value, combo.getSelectedItem());
      assertEquals(0, combo.getSelectedIndex());

      value = "none";
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());
   }

   public void testSetValueWithBlank() throws Exception {
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      combo.putClientProperty(SwingBinder.KEY_PROPERTY, "key");

      assertNull(componentBinder.bind(binder, combo, (FieldMetadata) null));
      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            combo, dataMeta));

      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(1);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(1, combo.getSelectedIndex());

      value = new MockBean("none", "None");
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());
   }

   public void testSetValueWithBlankUsingString() throws Exception {
      JComboBox combo = SwingUtils.newStringCombo();
      combo.putClientProperty(SwingBinder.BLANK_PROPERTY, Boolean.TRUE);
      MockSwingBinder binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      DataProviderMetadata dataMeta = (DataProviderMetadata) form.getFormMetadata()
            .getDataProviderMetadatas().get(
                  new MethodEntry(form.getMethod("someDataProvider")));
      WidgetBinder componentBinder = binder.getWidgetBinder(combo);
      dataMeta.setResetSelection(true);

      assertNotNull(boundField = (BoundField) componentBinder.bind(binder,
            combo, dataMeta));

      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      Object value = model.getElementAt(3);
      boundField.setValue(new String(value.toString()));
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(1);
      boundField.setValue(value);
      assertEquals(value, combo.getSelectedItem());
      assertEquals(1, combo.getSelectedIndex());

      value = model.getElementAt(3);
      boundField.setValue(new String(value.toString()));
      assertEquals(value, combo.getSelectedItem());
      assertEquals(3, combo.getSelectedIndex());

      value = model.getElementAt(1);
      boundField.setValue(new String(value.toString()));
      assertEquals(value, combo.getSelectedItem());
      assertEquals(1, combo.getSelectedIndex());

      value = "none";
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());

      value = model.getElementAt(0);
      boundField.setValue(value);
      assertNull(combo.getSelectedItem());
      assertEquals(-1, combo.getSelectedIndex());
   }
}
