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

import dev.neongenesis.GenesisTestCase;

public class BooleanFormatterTest extends GenesisTestCase {
   private static final String TRUE_VALUE = "a";
   private static final String FALSE_VALUE = "b";
   private static final String NULL_VALUE = "c";

   public void testDefaultConstructor() {
      testFormat(new BooleanFormatter(), BooleanFormatter.TRUE, 
            BooleanFormatter.FALSE, BooleanFormatter.FALSE);
   }

   public void testTwoStringsConstructor() {
      testFormat(new BooleanFormatter(TRUE_VALUE, FALSE_VALUE), TRUE_VALUE, 
            FALSE_VALUE, FALSE_VALUE);
   }

   public void testThreeStringsConstructor() {
      testFormat(new BooleanFormatter(TRUE_VALUE, FALSE_VALUE, NULL_VALUE), 
            TRUE_VALUE, FALSE_VALUE, NULL_VALUE);
   }

   private void testFormat(BooleanFormatter formatter, String trueValue, 
         String falseValue, String nullValue) {
      assertEquals(formatter.format(Boolean.TRUE), trueValue);
      assertEquals(formatter.format(Boolean.FALSE), falseValue);
      assertEquals(formatter.format(null), nullValue);
   }
}