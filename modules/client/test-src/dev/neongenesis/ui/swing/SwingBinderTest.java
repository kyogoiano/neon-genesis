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
package dev.neongenesis.ui.swing;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import dev.neongenesis.GenesisTestCase;

public class SwingBinderTest extends GenesisTestCase {
   public SwingBinderTest() {
      super("Swing Binder Unit Test");
   }
   
   public void testBindAndUnbindDefaultButtonNPE() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());
      try {
         binder.bind();
         binder.unbindDefaultButton();
         binder.bindDefaultButton(null);
         binder.unbindDefaultButton(null);
         binder.bindDefaultButton();
         binder.unbind();
         
         JRootPane frame = new JRootPane();
         final JButton defaultButton = new JButton();
         binder = new SwingBinder(frame, new Object());
         binder.bind();
         
         frame.getRootPane().setDefaultButton(defaultButton);
         frame.getRootPane().setDefaultButton(null);
      } catch(NullPointerException ex) {
         fail("NullPointerException should not be thrown for null default button.");
      }
   }
   
   public void testBindDefaultButton() {
      JRootPane frame = new JRootPane();
      final JButton defaultButton = new JButton();
      
      SwingBinder binder = new SwingBinder(frame, new Object());
      frame.getRootPane().setDefaultButton(defaultButton);
      
      binder.bind();
      assertSame(defaultButton, binder.getDefaultButton());
      
      frame.getRootPane().setDefaultButton(new JButton());
      assertNotSame(defaultButton, binder.getDefaultButton());
   }
   
   public void testIfDefaultButtonCreatesListener() {
      JRootPane frame = new JRootPane();
      final JButton defaultButton = new JButton();
      final ActionListener[] listeners = new ActionListener[1];
      
      SwingBinder binder = new SwingBinder(frame, new Object()) {
         protected ActionListener createDefautButtonListener() {
            listeners[0] = super.createDefautButtonListener();

            return listeners[0];
         }
      };

      frame.getRootPane().setDefaultButton(defaultButton);

      binder.bind();
      int length = defaultButton.getActionListeners().length;
      
      assertEquals(listeners.length, length);

      binder.unbindDefaultButton();
      assertEquals(length - 1, defaultButton.getActionListeners().length);

      binder.bindDefaultButton();
      assertEquals(listeners.length, length);
      
      final JButton newDefaultButton = new JButton();
      frame.getRootPane().setDefaultButton(newDefaultButton);
      assertEquals(length - 1, defaultButton.getActionListeners().length);
      assertEquals(length, newDefaultButton.getActionListeners().length);
      
      binder.unbindDefaultButton();
      assertEquals(length - 1, newDefaultButton.getActionListeners().length);
      
      binder.bindDefaultButton();
      assertEquals(length - 1, defaultButton.getActionListeners().length);
      assertEquals(length, newDefaultButton.getActionListeners().length);
      
      frame.getRootPane().setDefaultButton(null);
      assertEquals(length - 1, newDefaultButton.getActionListeners().length);
   }
   
   public void testDefaultButtonNPE() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());

      try {
         binder.bind();
      } catch (NullPointerException ex) {
         fail("NullPointerException should not be thrown for null RootPane.");
      }
   }
   
   public void testDefaultButton() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());
      binder.bind();
      assertNull(binder.getDefaultButton());
      
      JRootPane frame = new JRootPane();
      JButton defaultButton = new JButton();
      
      binder = new SwingBinder(frame, new Object());
      binder.bind();
      assertNull(binder.getDefaultButton());
      
      frame.getRootPane().setDefaultButton(defaultButton);
      binder.bindDefaultButton();
      assertNotNull(binder.getDefaultButton());
      assertSame(defaultButton, binder.getDefaultButton());
   }
   
   public void testHasDefaultButton() {
      SwingBinder binder = new SwingBinder(new JPanel(), new Object());
      assertFalse(binder.hasDefaultButton());
      
      JRootPane frame = new JRootPane();
      JButton defaultButton = new JButton();
      
      binder = new SwingBinder(frame, new Object());
      binder.bind();
      assertFalse(binder.hasDefaultButton());
      
      frame.getRootPane().setDefaultButton(defaultButton);
      binder.bindDefaultButton();
      assertTrue(binder.hasDefaultButton());
   }
}
