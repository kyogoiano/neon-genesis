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
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;

import dev.neongenesis.mockobjects.MockBean;
import dev.neongenesis.ui.swing.factory.SwingFactory;

public class SwingUtils {

   public static AbstractButton newButton() {
      AbstractButton component = new AbstractButton() {
      };

      component.setModel(new DefaultButtonModel());

      return component;
   }

   public static JComboBox newStringCombo() {
      JComboBox combo = new JComboBox();
      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      model.addElement("one");
      model.addElement("two");
      model.addElement("three");
      model.addElement("four");
      model.addElement("five");

      return combo;
   }
   
   public static JComboBox newCombo() {
      JComboBox combo = new JComboBox();
      DefaultComboBoxModel model = (DefaultComboBoxModel) combo.getModel();

      model.addElement(new MockBean("one", "One"));
      model.addElement(new MockBean("two", "Two"));
      model.addElement(new MockBean("three", "Three"));
      model.addElement(new MockBean("four", "Four"));
      model.addElement(new MockBean("five", "Five"));

      return combo;
   }

   public static JList newStringList() {
      JList list = new JList(new DefaultListModel());
      DefaultListModel model = (DefaultListModel) list.getModel();

      model.addElement("one");
      model.addElement("two");
      model.addElement("three");
      model.addElement("four");
      model.addElement("five");

      return list;
   }

   public static JList newList() {
      JList list = new JList(new DefaultListModel());
      DefaultListModel model = (DefaultListModel) list.getModel();

      model.addElement(new MockBean("one", "One"));
      model.addElement(new MockBean("two", "Two"));
      model.addElement(new MockBean("three", "Three"));
      model.addElement(new MockBean("four", "Four"));
      model.addElement(new MockBean("five", "Five"));

      return list;
   }

   public static JTable newTable() {
      return SwingFactory.createTable(new String[] { "key", "value", "bean.key" },
            new String[] { "key", "value", "bean.key" });
   }
}
