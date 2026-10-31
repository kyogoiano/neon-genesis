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

import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;
import javax.swing.text.JTextComponent;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.binding.BoundField;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.swing.MockSwingBinder;

public class JTextComponentBinderTest extends GenesisTestCase {
   private class JTextField extends javax.swing.JTextField {
      public void processKeyEvent(KeyEvent e) {
         super.processKeyEvent(e);
      }
   }

   private JTextField text;
   private MockSwingBinder binder;
   private WidgetBinder componentBinder;
   private BoundField boundField;
   private MockForm form;
   private FieldMetadata fieldMeta;

   public JTextComponentBinderTest() {
      super("JText Component Binder Unit Test");
   }

   protected void setUp() throws Exception {
      text = new JTextField();
      binder = new MockSwingBinder(new JPanel(), form = new MockForm());
      binder.register("text", text);
      componentBinder = binder.getWidgetBinder(text);
      fieldMeta = form.getFormMetadata().getFieldMetadata("stringField");
   }

   public void testSetValue() throws Exception {
      assertTrue(componentBinder instanceof JTextComponentBinder);
      assertNull(componentBinder
            .bind(binder, text, (DataProviderMetadata) null));

      boundField = componentBinder.bind(binder, text, fieldMeta);
      assertNotNull(boundField);

      boundField.setValue("someValue");
      assertEquals("someValue", text.getText());

      boundField.setValue("  ");
      assertEquals("  ", text.getText());

      boundField.setValue(null);
      assertEquals("", text.getText());
   }

   public void testUpdateValue() throws Exception {
      assertNull(componentBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, text, fieldMeta));

      text.setText("someValue");
      simulateFocusLost(text);
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("  ");
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText(null);
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }
   
   public void testUpdateValueAsYouType() throws Exception {
      text.putClientProperty(AbstractBinder.BINDING_STRATEGY_PROPERTY, 
            AbstractBinder.BINDING_STRATEGY_AS_YOU_TYPE);
      assertNull(componentBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, text, fieldMeta));

      simulateTyping(text, "someValue");
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      simulateTyping(text, " ");
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }

   public void testUpdateValueWithoutTrim() throws Exception {
      binder.registerWidgetBinder("text", new JTextComponentBinder(false));
      componentBinder = binder.getWidgetBinder(text);
      
      assertNull(componentBinder.bind(binder, text,
            (DataProviderMetadata) null));
      assertNotNull(componentBinder.bind(binder, text, fieldMeta));

      text.setText("someValue");
      simulateFocusLost(text);
      assertEquals("someValue", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText("  ");
      simulateFocusLost(text);
      assertEquals("  ", binder.get("populateForm(FieldMetadata,Object)"));

      text.setText(null);
      simulateFocusLost(text);
      assertEquals("", binder.get("populateForm(FieldMetadata,Object)"));
   }

   protected void simulateFocusLost(JTextComponent component) {
      FocusListener[] listeners = component.getFocusListeners();
      if (listeners.length == 0) {
         return;
      }

      FocusEvent event = new FocusEvent(component, FocusEvent.FOCUS_LOST);
      for (int i = 0; i < listeners.length; i++) {
         listeners[i].focusLost(event);
      }
   }

   private void simulateTyping(final JTextField component, 
         final String value) throws InterruptedException, InvocationTargetException {
      component.setText("");
      final char[] chars = value.toCharArray();

      for (int i = 0; i < chars.length; i++) {
         final char c = chars[i];

         EventQueue.invokeAndWait(new Runnable() {
            public void run() {
               component.processKeyEvent(new KeyEvent(component, KeyEvent.KEY_TYPED, 
                     EventQueue.getMostRecentEventTime(), 0, KeyEvent.VK_UNDEFINED, 
                     c));
            }
         });
      }
   }
}
