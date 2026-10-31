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
package dev.neongenesis.ui.binding;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import dev.neongenesis.util.Bundle;


public class MapLookupStrategy implements LookupStrategy {
   private Map components = new HashMap();
   private Map identityMap = new IdentityHashMap();

   public Object lookup(Object object, String name) {
      return components.get(name);
   }

   public Object register(String name, Object object) {
      return register(name, object, false);
   }

   protected Object register(String name, Object object, boolean onlyIfMissing) {
      if (identityMap.containsKey(object)) {
         if (onlyIfMissing) {
            return null;
         }

         throw new IllegalArgumentException(Bundle.getMessage(MapLookupStrategy.class,
               "COMPONENT_X_IS_ALREADY_BOUND", object)); // NOI18N
      }

      identityMap.put(object, name);
      final Object oldValue = components.put(name, object);
      if (oldValue != null) {
         identityMap.remove(oldValue);
      }

      return object;
   }

   protected void registerMap(String name, Object object) {
      if (name == null || components.containsKey(name)) {
         return;
      }

      register(name, object, true);
   }

   public String getName(Object object) {
      return (String) identityMap.get(object);
   }
}
