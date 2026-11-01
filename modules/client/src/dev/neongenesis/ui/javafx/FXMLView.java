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

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.util.Callback;

public final class FXMLView<C> implements AutoCloseable {
   private final Node root;
   private final C controller;
   private final JavaFXBinder binder;

   private FXMLView(Node root, C controller, JavaFXBinder binder) {
      this.root = root;
      this.controller = controller;
      this.binder = binder;
   }

   public static <C> FXMLView<C> load(URL location, Object form)
         throws IOException {
      return load(location, form, null);
   }

   public static <C> FXMLView<C> load(URL location, Object form,
         Callback<Class<?>, Object> controllerFactory) throws IOException {
      Objects.requireNonNull(location, "location");
      Objects.requireNonNull(form, "form");

      FXMLLoader loader = new FXMLLoader(location);
      if (controllerFactory != null) {
         loader.setControllerFactory(controllerFactory);
      }

      Object loadedRoot = loader.load();
      if (!(loadedRoot instanceof Node root)) {
         throw new IOException("FXML root must be a JavaFX Node");
      }

      C controller = loader.getController();
      if (controller == null) {
         throw new IOException("FXML document must declare a controller");
      }

      JavaFXBinder binder = new JavaFXBinder(root, form, controller);
      binder.bind();
      return new FXMLView<>(root, controller, binder);
   }

   public Node getRoot() {
      return root;
   }

   public C getController() {
      return controller;
   }

   public JavaFXBinder getBinder() {
      return binder;
   }

   @Override
   public void close() {
      binder.unbind();
   }
}
