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
package dev.neongenesis.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Bundle {
   public static String getMessage(Class clazz, String key) throws
         MissingResourceException {
      return getMessage(clazz, key, null);
   }

   public static String getMessage(Class clazz, String key, Object param1)
         throws MissingResourceException {
      return getMessage(clazz, key, new Object[] {param1});
   }

   public static String getMessage(Class clazz, String key, Object param1,
         Object param2) throws MissingResourceException {
      return getMessage(clazz, key, new Object[] {param1, param2});
   }

   public static String getMessage(Class clazz, String key, Object[] params)
         throws MissingResourceException {
      return MessageFormat.format(getBundleString(clazz, key), params);
   }

   public static ResourceBundle getBundle(Class clazz) throws
         MissingResourceException {
      String name = findName(clazz);
      return getBundle(name, Locale.getDefault(), clazz.getClassLoader());
   }

   protected static String getBundleString(Class clazz, String key) throws
         MissingResourceException {
      return getBundle(clazz).getString(key);
   }

   /** Finds package name for given class */
   protected static String findName(Class clazz) {
      String pref = clazz.getName();
      int last = pref.lastIndexOf('.');

      if (last >= 0) {
         pref = pref.substring(0, last + 1);

         return pref + "Bundle"; // NOI18N
      } else {
         // base package, search for bundle
         return "Bundle"; // NOI18N
      }
   }

   public static final ResourceBundle getBundle(String baseName, Locale locale,
         ClassLoader loader) throws MissingResourceException {
      ResourceBundle b = ResourceBundle.getBundle(baseName, locale, loader);

      if (b != null) {
         return b;
      } else {
         throw new MissingResourceException("No such bundle " + baseName, // NOI18N
               baseName, null);
      }
   }
}
