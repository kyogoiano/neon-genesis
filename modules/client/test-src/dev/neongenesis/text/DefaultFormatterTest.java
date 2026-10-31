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

public class DefaultFormatterTest extends GenesisTestCase {
   public void testDefaultConstructor() {
      testFormat(new DefaultFormatter(), "");
   }

   public void testStringConstructor() {
      String defaultValue = "default";
      testFormat(new DefaultFormatter(defaultValue), defaultValue);
   }

   private void testFormat(DefaultFormatter formatter, String defaultValue) {
      // Test for null
      assertSame(formatter.format(null), defaultValue);

      // Test for an object
      String text = "text";
      assertEquals(formatter.format(text), text);
   }
}