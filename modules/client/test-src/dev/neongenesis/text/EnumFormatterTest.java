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

public class EnumFormatterTest extends GenesisTestCase {
   enum TestEnum {
      A,
      B
   }

   public void testFormat() {
      EnumFormatter formatter = new EnumFormatter();

      // Test for null
      assertEquals(formatter.format(null), "");

      // Test for an Enum instance
      assertEquals(TestEnum.A.name(),
            formatter.format(TestEnum.A));

      // Test for an Enum instance with a defined key
      String text = "text";
      putInResourceBundle(getConstantName(TestEnum.B), text);
      assertEquals(text, formatter.format(TestEnum.B));
   }

   private String getConstantName(Enum<?> en) {
      String className = en.getDeclaringClass().getName();
      className = className.substring(className.lastIndexOf('.') + 1);
      className = className.substring(className.lastIndexOf('$') + 1);

      return className + '.' + en.name();
   }
}
