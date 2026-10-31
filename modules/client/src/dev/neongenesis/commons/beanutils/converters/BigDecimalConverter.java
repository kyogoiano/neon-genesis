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
package dev.neongenesis.commons.beanutils.converters;

import java.math.BigDecimal;
import java.util.StringTokenizer;
import org.apache.commons.beanutils.Converter;

public class BigDecimalConverter implements Converter {
   private org.apache.commons.beanutils.converters.BigDecimalConverter converter = 
               new org.apache.commons.beanutils.converters.BigDecimalConverter(null);
   
   public Object convert(Class clazz, Object obj) {
      if (obj instanceof BigDecimal) {
         return obj;
      }

      if (obj == null || !(obj instanceof String)) {
         return converter.convert(clazz, obj);
      }

      final String s = obj.toString();

      if (s.trim().length() == 0) {
         return converter.convert(clazz, obj);
      }

      final StringTokenizer t = new StringTokenizer(s, "."); // NOI18N
      final StringBuffer ret = new StringBuffer(s.length());

      while (t.hasMoreTokens()) {
         ret.append(t.nextToken());
      }

      int i;
      if ((i = ret.indexOf(",")) != -1) { // NOI18N
         ret.setCharAt(i,  '.');
      }

      try {
         return new BigDecimal(ret.toString().trim());
      } catch (NumberFormatException nfe) {
         return null;
      }
   }   
}