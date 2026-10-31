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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;
import dev.neongenesis.ui.swing.SwingBinder;
import dev.neongenesis.ui.swing.components.AbstractComponentBinder.AbstractBoundMember;

public class AbstractComponentBinderTest extends GenesisTestCase {
   private MockForm form;
   private JTextField comp1;
   private JTextField comp2;
   private MockSwingBinder binder;
   private AbstractComponentBinder componentBinder;
   private AbstractBoundMember boundMember;
   private ActionMetadata actionMeta;

   public AbstractComponentBinderTest() {
      super("Abstract Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      form = new MockForm();
      comp1 = new JTextField();
      comp2 = new JTextField();

      binder = new MockSwingBinder(new JPanel(), form);
      comp1.putClientProperty(SwingBinder.ENABLED_GROUP_PROPERTY, comp2);
      comp1.putClientProperty(SwingBinder.VISIBLE_GROUP_PROPERTY, comp2);

      componentBinder = new AbstractComponentBinder() {
      };
      boundMember = componentBinder.new AbstractBoundMember(binder, comp1) {
      };
      actionMeta = (ActionMetadata) form.getFormMetadata().getActionMetadatas().get("someAction");
   }

   public void testEnabled() {
      assertTrue(comp1.isEnabled());
      assertTrue(comp2.isEnabled());

      boundMember.setEnabled(false);

      assertFalse(comp1.isEnabled());
      assertFalse(comp2.isEnabled());
   }

   public void testVisible() {
      assertTrue(comp1.isVisible());
      assertTrue(comp2.isVisible());

      boundMember.setVisible(false);

      assertFalse(comp1.isVisible());
      assertFalse(comp2.isVisible());
   }

   public void testSimpleActionBinding() {
      assertNotNull(componentBinder.bind(binder, comp1, (ActionMetadata) 
            form.getFormMetadata().getActionMetadatas().get("someAction")));
      comp1.postActionEvent();
      assertSame(actionMeta, binder.get("invokeFormAction(ActionMetadata)"));
   }
   
   public void testMultipleActions() {
      assertNull(componentBinder.bind(binder, new InvalidMultipleActions(), 
            (ActionMetadata)form.getFormMetadata().getActionMetadatas()
            .get("someAction")));
      
      ValidMultipleActions valid = new ValidMultipleActions();
      assertNotNull(componentBinder.bind(binder, valid, (ActionMetadata) 
            form.getFormMetadata().getActionMetadatas().get("someAction")));

      binder.put("invokeFormAction(ActionMetadata)", null);
      valid.fireActionOneEvent();
      assertNull(binder.get("invokeFormAction(ActionMetadata)"));

      valid.fireActionEvent();
      assertSame(actionMeta, binder.get("invokeFormAction(ActionMetadata)"));
   }

   public static class InvalidMultipleActions extends JComponent {
      private final Collection actionOneListeners = new ArrayList();
      private final Collection actionTwoListeners = new ArrayList();

      public void addActionOneListener(ActionListener listener) {
         actionOneListeners.add(listener);
      }

      public void removeActionOneListener(ActionListener listener) {
         actionOneListeners.remove(listener);
      }

      public Collection getActionOneListeners() {
         return actionOneListeners;
      }

      public void fireActionOneEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "actionOne");

         for (Iterator i = actionOneListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }
      
      public void addActionTwoListener(ActionListener listener) {
         actionTwoListeners.add(listener);
      }

      public void removeActionTwoListener(ActionListener listener) {
         actionTwoListeners.remove(listener);
      }

      public Collection getActionTwoListeners() {
         return actionTwoListeners;
      }

      public void fireActionTwoEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "actionTwo");

         for (Iterator i = actionTwoListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }

   }
   
   public static class ValidMultipleActions extends JComponent {
      private final Collection actionOneListeners = new ArrayList();
      private final Collection actionListeners = new ArrayList();

      public void addActionOneListener(ActionListener listener) {
         actionOneListeners.add(listener);
      }

      public void removeActionOneListener(ActionListener listener) {
         actionOneListeners.remove(listener);
      }

      public Collection getActionOneListeners() {
         return actionOneListeners;
      }

      public void fireActionOneEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "actionOne");

         for (Iterator i = actionOneListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }
      
      public void addActionListener(ActionListener listener) {
         actionListeners.add(listener);
      }

      public void removeActionListener(ActionListener listener) {
         actionListeners.remove(listener);
      }

      public Collection getActionTwoListeners() {
         return actionListeners;
      }

      public void fireActionEvent() {
         ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
               "action");

         for (Iterator i = actionListeners.iterator(); i.hasNext();) {
            ((ActionListener) i.next()).actionPerformed(e);
         }
      }

   }
}
