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

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public final class GenesisUtils {

   public static Map normalizeMap(final Map map) {
      map.remove("class"); // NOI18N
      map.remove("context"); // NOI18N
      return map;
   }

   /**
    * Reads a string line containing <code>key1=value1 key2=value2</code>
    * and makes a Map.
    * 
    * @param attributesLine
    * @return
    */
   public static Map getAttributesMap(final String attributesLine) {
      return getAttributesMap(attributesLine, " \t\n\r\f", "="); // NOI18N
   }
   
   public static Map getAttributesMap(final String attributesLine, String pairsDelim, String keyValueDelim) {
      final StringTokenizer pairsTok = new StringTokenizer(attributesLine, pairsDelim);
      final Map attributesMap = new HashMap();
      while (pairsTok.hasMoreTokens()) {
         final StringTokenizer keyValueTok = new StringTokenizer(
               pairsTok.nextToken(), keyValueDelim);
         if (keyValueTok.countTokens() == 2) {
            attributesMap.put(keyValueTok.nextToken().trim(), keyValueTok
                  .nextToken().trim());
         }
      }
      return attributesMap;
   }
   
   public static Map getAttributesMap(String[] values) {
      final Map attributesMap = new HashMap(values.length);
      for (int i = 0; i < values.length; i++) {
         String[] value = values[i].split("\\s*=\\s*"); // NOI18N
        
         if (value.length != 2) {
            throw new IllegalArgumentException(Bundle.getMessage(
                  GenesisUtils.class, "INVALID_ARGUMENT_X", values[i])); // NOI18N
         }
         attributesMap.put(value[0].trim(), value[1].trim());
      }
      return attributesMap;
   }
   
   public static boolean isBlank(String value) {
      return value == null || value.trim().length() == 0;
   }
}
