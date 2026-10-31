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

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.GroupBinder;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;
import dev.neongenesis.ui.swing.SwingBinder;

public class ButtonGroupBinderTest extends GenesisTestCase {   
   private AbstractButton button1;
   private AbstractButton button2;
   private MockSwingBinder binder;
   private MockForm form;
   private ButtonGroup group;
   private GroupBinder groupBinder;
   private FieldMetadata fieldMeta;
   
   public ButtonGroupBinderTest() {
      super("Button Group Binder Unit Test");
   }

   protected void setUp() throws Exception {
      button1 = SwingUtils.newButton();
      button2 = SwingUtils.newButton();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
      group = new ButtonGroup();
      group.add(button1);
      group.add(button2);
   }

   public void testButtonGroupWithClientProperty() {
      groupBinder = binder.getGroupBinder(group);
      assertTrue(groupBinder instanceof ButtonGroupBinder);
      
      groupBinder.bind(binder, group, fieldMeta);
      
      Object value1 = new Object();
      button1.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value1);

      Object value2 = new Object();
      button2.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value2);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      group.setSelected(button1.getModel(), true);
      button1.doClick();
      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      assertSame(value1, binder.get("populateForm(FieldMetadata,Object)"));

      group.setSelected(button2.getModel(), true);
      button2.doClick();
      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      assertSame(value2, binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testButtonGroupWithActionCommand() {
      binder.registerButtonGroup("group", group, new ButtonGroupBinder(true));
      groupBinder = binder.getGroupBinder(group);
      groupBinder.bind(binder, group, fieldMeta);
      
      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      group.setSelected(button1.getModel(), true);
      button1.doClick();
      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      assertSame(value1, binder.get("populateForm(FieldMetadata,Object)"));

      group.setSelected(button2.getModel(), true);
      button2.doClick();
      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      assertSame(value2, binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testButtonGroupWithName() {
      groupBinder = binder.getGroupBinder(group);
      groupBinder.bind(binder, group, fieldMeta);
      
      String value1 = "value1";
      button1.setName(value1);

      String value2 = "value2";
      button2.setName(value2);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      group.setSelected(button1.getModel(), true);
      button1.doClick();
      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      assertSame(value1, binder.get("populateForm(FieldMetadata,Object)"));

      group.setSelected(button2.getModel(), true);
      button2.doClick();
      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      assertSame(value2, binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testSetValueWithClientProperty() throws Exception {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);
      
      Object value1 = new Object();
      button1.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value1);

      Object value2 = new Object();
      button2.putClientProperty(SwingBinder.BUTTON_GROUP_SELECTION_VALUE,
            value2);

      assertNotNull(boundField);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value1);

      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value2);

      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      boundField.setValue(new Object());

      assertFalse(button1.getModel().isSelected());
      // It's not possible to unselect all after first selection
      assertTrue(button2.getModel().isSelected());
   }

   public void testSetValueWithActionCommand() throws Exception {
      binder.registerButtonGroup("group", group, new ButtonGroupBinder(true));
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);
      
      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

      assertNotNull(boundField);

      assertFalse(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value1);

      assertTrue(button1.getModel().isSelected());
      assertFalse(button2.getModel().isSelected());

      boundField.setValue(value2);

      assertFalse(button1.getModel().isSelected());
      assertTrue(button2.getModel().isSelected());

      boundField.setValue("bogusvalue");

      assertFalse(button1.getModel().isSelected());
      // It's not possible to unselect all after first selection
      assertTrue(button2.getModel().isSelected());
   }

   public void testSetEnabled() {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);

      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

      assertTrue(button1.isEnabled());
      assertTrue(button2.isEnabled());

      boundField.setEnabled(false);

      assertFalse(button1.isEnabled());
      assertFalse(button2.isEnabled());

      boundField.setEnabled(true);

      assertTrue(button1.isEnabled());
      assertTrue(button2.isEnabled());
   }
   
   public void testSetVisible() {
      groupBinder = binder.getGroupBinder(group);
      BoundField boundField = groupBinder.bind(binder, group, fieldMeta);
      
      String value1 = "value1";
      button1.setActionCommand(value1);

      String value2 = "value2";
      button2.setActionCommand(value2);

      assertTrue(button1.isVisible());
      assertTrue(button2.isVisible());

      boundField.setVisible(false);

      assertFalse(button1.isVisible());
      assertFalse(button2.isVisible());

      boundField.setVisible(true);

      assertTrue(button1.isVisible());
      assertTrue(button2.isVisible());
   }
}
