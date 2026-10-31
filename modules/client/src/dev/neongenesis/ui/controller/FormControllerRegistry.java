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
package dev.neongenesis.ui.controller;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class FormControllerRegistry {
   private static final FormControllerRegistry instance = new FormControllerRegistry();

   private final Map registry = new WeakHashMap();

   private FormControllerRegistry() {
   }

   public static FormControllerRegistry getInstance() {
      return instance;
   }

   public FormController get(Object form) {
      WeakReference ref = (WeakReference) registry.get(form);
      if (ref == null) {
         return null;
      }

      return (FormController) ref.get();
   }

   public void register(Object form, FormController controller) {
      // controller is stored as a weak reference because controller
      // references the form itself. When the controller is garbage 
      // collected, the form itself can be removed from the map.
      registry.put(form, new WeakReference(controller));
   }
}
