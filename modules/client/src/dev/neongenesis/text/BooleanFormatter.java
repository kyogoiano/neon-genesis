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
package dev.neongenesis.text;

public class BooleanFormatter implements Formatter {
   public static final String TRUE = "true"; // NOI18N
   public static final String FALSE = "false"; // NOI18N

   private final String trueValue;
   private final String falseValue;
   private final String nullValue;

   public BooleanFormatter() {
      this(TRUE, FALSE, FALSE);
   }

   public BooleanFormatter(String trueValue, String falseValue) {
      this(trueValue, falseValue, falseValue);
   }

   public BooleanFormatter(String trueValue, String falseValue, 
         String nullValue) {
      this.trueValue = trueValue;
      this.falseValue = falseValue;
      this.nullValue = nullValue;
   }

   public String format(Object o) {
      return o == null ? nullValue : (((Boolean)o).booleanValue() ? trueValue :
            falseValue);
   }
}