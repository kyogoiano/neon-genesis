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
package dev.neongenesis.resolvers;

import dev.neongenesis.GenesisTestCase;

public class StringEmptyResolverTest extends GenesisTestCase {
   public void testDefaultConstructor() {
      final StringEmptyResolver resolver = new StringEmptyResolver();
      testIsEmpty(resolver, true);
   }

   public void testTrimFalse() {
      final StringEmptyResolver resolver = new StringEmptyResolver();
      resolver.setTrim(false);
      testIsEmpty(resolver, false);
   }

   private void testIsEmpty(StringEmptyResolver resolver, boolean trim) {
      assertEquals(resolver.isTrim(), trim);

      // Test for null
      assertTrue(resolver.isEmpty(null));

      // Test for non-empty String
      assertFalse(resolver.isEmpty("test"));

      // Test for zero-length String
      assertTrue(resolver.isEmpty(""));

      // Test for space-only String
      assertEquals(trim, resolver.isEmpty("   "));
   }
}