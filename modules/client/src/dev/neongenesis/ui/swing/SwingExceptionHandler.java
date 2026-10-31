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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import dev.neongenesis.ui.UIUtils;
import dev.neongenesis.ui.binding.AbstractDispatcherExceptionHandler;

import dev.neongenesis.util.Bundle;
import org.apache.commons.logging.LogFactory;

public class SwingExceptionHandler extends AbstractDispatcherExceptionHandler {
   private final Component root;

   public SwingExceptionHandler(Component root) {
      this.root = root;
   }

   public Component getRoot() {
      return root;
   }

   protected void showWarningMessageDialog(final String title, final String message) {
      showMessageDialog(title, message, JOptionPane.WARNING_MESSAGE);
   }

   protected void showErrorMessageDialog(final String title,
         final String message, Throwable throwable) {
      showMessageDialog(title, createStackTracePanel(message, throwable), JOptionPane.ERROR_MESSAGE);
   }
   
   protected Object createStackTracePanel(String message, Throwable throwable) {
      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

      JLabel messageLabel = new JLabel(message);
      messageLabel.setFont(messageLabel.getFont().deriveFont(Font.BOLD));
      messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      JLabel stackLabel = new JLabel(UIUtils.getInstance().getBundle()
            .getString("ErrorReporterDialog.stackTrace")); // NOI18N
      stackLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

      JTextArea stackTextArea = new JTextArea(getStackTrace(throwable));
      stackTextArea.setEditable(false);
      stackTextArea.setColumns(50);
      stackTextArea.setRows(10);

      panel.add(messageLabel);
      panel.add(Box.createRigidArea(new Dimension(0, 5)));
      panel.add(stackLabel);
      panel.add(Box.createRigidArea(new Dimension(0, 5)));
      panel.add(new JScrollPane(stackTextArea));

      return panel;
   }

   protected void showMessageDialog(final String title, final Object message,
         final int type) {
      if (EventQueue.isDispatchThread()) {
         JOptionPane.showMessageDialog(root, message, title, type);
         
         return;
      }
      
      try {
         EventQueue.invokeAndWait(new Runnable() {
            public void run() {
               JOptionPane.showMessageDialog(root, message, title, type);
            }
         });
      } catch (HeadlessException ex) {
         LogFactory.getLog(getClass()).error(Bundle.getMessage(
               SwingExceptionHandler.class, "UNKNOWN_EXCEPTION"), ex); // NOI18N
      } catch (InterruptedException ex) {
         LogFactory.getLog(getClass()).error(Bundle.getMessage(
               SwingExceptionHandler.class, "UNKNOWN_EXCEPTION"), ex); // NOI18N
      } catch (InvocationTargetException ex) {
         LogFactory.getLog(getClass()).error(Bundle.getMessage(
               SwingExceptionHandler.class, "UNKNOWN_EXCEPTION"), ex); // NOI18N
      }
   }
}