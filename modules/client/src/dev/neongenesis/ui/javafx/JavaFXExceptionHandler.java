/*
 * Neon Genesis
 * Copyright (C) 2026 Leandro Cardoso de Andrade (@kyogoiano <leandro.ueg@gmail.com>)
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
package dev.neongenesis.ui.javafx;

import dev.neongenesis.ui.UIUtils;
import dev.neongenesis.ui.binding.AbstractDispatcherExceptionHandler;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.apache.commons.logging.LogFactory;

/**
 * Default {@link dev.neongenesis.ui.binding.ExceptionHandler} for OpenJFX views.
 */
public class JavaFXExceptionHandler extends AbstractDispatcherExceptionHandler {
   private final Node root;

   public JavaFXExceptionHandler(Node root) {
      this.root = root;
   }

   public Node getRoot() {
      return root;
   }

   @Override
   protected void showWarningMessageDialog(final String title, final String message) {
      if (Platform.isFxApplicationThread()) {
         Alert alert = new Alert(AlertType.WARNING);
         alert.setTitle(title);
         alert.setHeaderText(null);
         alert.setContentText(message);
         alert.showAndWait();
      } else {
         LogFactory.getLog(getClass()).warn(title + ": " + message);
      }
   }

   @Override
   protected void showErrorMessageDialog(final String title, final String message,
         final Throwable throwable) {
      if (Platform.isFxApplicationThread()) {
         Alert alert = new Alert(AlertType.ERROR);
         alert.setTitle(title);
         alert.setHeaderText(message);
         alert.setContentText(UIUtils.getInstance().getStackTrace(throwable));
         alert.showAndWait();
      } else {
         LogFactory.getLog(getClass()).error(title + ": " + message, throwable);
      }
   }
}
