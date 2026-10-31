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
package dev.neongenesis.reflection;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public final class ClassesCache {
   private static Map cache = new HashMap();

   static {
      put(Byte.TYPE.getName(), Byte.TYPE);
      put(Character.TYPE.getName(), Character.TYPE);
      put(Short.TYPE.getName(), Short.TYPE);
      put(Integer.TYPE.getName(), Integer.TYPE);
      put(Long.TYPE.getName(), Long.TYPE);
      put(Float.TYPE.getName(), Float.TYPE);
      put(Double.TYPE.getName(), Double.TYPE);
      put(Boolean.TYPE.getName(), Boolean.TYPE);
      put(Void.TYPE.getName(), Void.TYPE);
   }

   public static Class getClass(String className) throws ClassNotFoundException {
      WeakReference r = (WeakReference)cache.get(className);
      Class clazz = null;

      if (r == null || (clazz = (Class)r.get()) == null) {
         clazz = Class.forName(className);
         put(className, clazz);
      }

      return clazz;
   }

   private static void put(String className, Class clazz) {
      cache.put(className, new WeakReference(clazz));
   }
}