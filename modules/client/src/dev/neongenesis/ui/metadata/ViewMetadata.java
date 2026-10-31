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
package dev.neongenesis.ui.metadata;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import dev.neongenesis.reflection.ReflectionInvoker;

public class ViewMetadata {
   private final Class viewClass;

   private final Map beforeActions;
   private final Map afterActions;

   public ViewMetadata(final Class viewClass) {
      this.viewClass = viewClass;
      this.beforeActions = new HashMap();
      this.afterActions = new HashMap();
   }

   public Class getViewClass() {
      return viewClass;
   }

   public void addBeforeAction(final String actionName, final String methodName) {
      beforeActions.put(actionName, methodName);
   }

   public void addAfterAction(final String actionName, final String methodName) {
      afterActions.put(actionName, methodName);
   }

   public boolean invokeBeforeAction(final Object target, final String actionName)
         throws NoSuchMethodException, IllegalAccessException,
         ClassNotFoundException, InvocationTargetException {

      final String methodName = (String)beforeActions.get(actionName);

      if (methodName == null) {
         return true;
      }

      final Object result = ReflectionInvoker.getInstance().invoke(target,
            methodName);

      return !Boolean.FALSE.equals(result);
   }

   public void invokeAfterAction(final Object target, final String actionName)
         throws NoSuchMethodException, IllegalAccessException,
         ClassNotFoundException, InvocationTargetException {

      final String methodName = (String)afterActions.get(actionName);

      if (methodName == null) {
         return;
      }

      ReflectionInvoker.getInstance().invoke(target, methodName);
   }
}