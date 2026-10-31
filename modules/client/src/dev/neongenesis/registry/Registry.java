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
package dev.neongenesis.registry;

import java.util.Map;
import java.util.WeakHashMap;

public class Registry {
   private final Map registry = new WeakHashMap();

   public Object register(Class clazz, Object o) {
      return registry.put(clazz, o);
   }

   public void deregister() {
      registry.clear();
   }

   public void deregister(Class clazz) {
      registry.remove(clazz);
   }

   public Object get(Class clazz) {
      return registry.get(clazz);
   }

   protected Object searchHierarchy(Class clazz) {
      if (clazz == null) {
         return null;
      }

      Object o = null;

      if ((o = get(clazz)) != null) {
         return o;
      }

      final Class[] interfaces = clazz.getInterfaces();

      for (int i = 0; i < interfaces.length; i++) {
         if ((o = searchHierarchy(interfaces[i])) != null) {
            return o;
         }
      }

      return clazz.isInterface() ? null : 
            searchHierarchy(clazz.getSuperclass());
   }

   public Object get(final Class clazz, boolean superClass) {
      if (!superClass) {
         return get(clazz);
      }

      Object o = null;
      Class currentClass = clazz;

      while ((o = get(currentClass)) == null && !Object.class.equals(
            currentClass)) {
         currentClass = currentClass.getSuperclass();

         if (currentClass == null) {
            currentClass = Object.class;
         }
      }

      if (!Object.class.equals(currentClass)) {
         return o;
      }

      final Object hierarchyObject = searchHierarchy(clazz);

      return hierarchyObject == null ? o : hierarchyObject;
   }

   public Object get(Object o) {
      final Class clazz = o == null ? Object.class : o.getClass();
      return get(clazz, true);
   }
}