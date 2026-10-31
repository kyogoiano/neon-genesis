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
package dev.neongenesis.equality;

import dev.neongenesis.GenesisTestCase;

public class IdentityEqualityComparatorTest extends GenesisTestCase {
   public void testComparator() {
      IdentityEqualityComparator comparator = new IdentityEqualityComparator();

      String s = "1";
      Integer i = 1;

      assertTrue(comparator.equals(null, null));
      assertFalse(comparator.equals(null, s));
      assertFalse(comparator.equals(s, null));
      assertTrue(comparator.equals(s, s));
      assertFalse(comparator.equals(s, new String("1")));
      assertTrue(comparator.equals(i, i));
      assertFalse(comparator.equals(i, new Integer(1)));
      assertFalse(comparator.equals(s, i));
   }
}