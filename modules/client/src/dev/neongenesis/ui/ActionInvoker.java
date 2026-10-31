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
package dev.neongenesis.ui;

import dev.neongenesis.ui.controller.DefaultFormControllerFactory;
import dev.neongenesis.ui.controller.FormController;
import dev.neongenesis.ui.controller.FormControllerFactory;

public class ActionInvoker {
   public static void invoke(final Object target, final String actionName)
         throws Exception {
      getFormController(target).invokeAction(actionName, null);
   }

   public static void refresh(final Object target) throws Exception {
      getFormController(target).update();
   }

   private static FormController getFormController(final Object target) 
         throws Exception {
      
      final FormController controller;
      if (target instanceof FormControllerFactory) {
         controller = ((FormControllerFactory) target)
               .getFormController(target);
      } else {
         controller = new DefaultFormControllerFactory()
               .getFormController(target);
      }

      if (!controller.isSetup()) {
         controller.setup();
      }

      return controller;
   }
}