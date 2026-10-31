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

import java.util.Comparator;
import dev.neongenesis.GenesisTestCase;

public class EqualityComparatorAdapterTest extends GenesisTestCase {
   public void testComparator() {
      Comparator adaptee = (o1, o2) -> {
         Comparable c1 = (Comparable)o1;
         Comparable c2 = (Comparable)o2;

         return c1 != null ? (c2 == null ? -1 : -c1.compareTo(c2)) :
            (c2 == null ? 0 : 1);
      };
      
      EqualityComparatorAdapter comparator = new EqualityComparatorAdapter(
            adaptee);

      String s1 = "a";
      String s2 = "b";
      Integer i1 = 1;
      Integer i2 = 2;

      assertTrue(comparator.equals(null, null));
      assertFalse(comparator.equals(null, s1));
      assertFalse(comparator.equals(s1, null));

      assertTrue(comparator.equals(s1, s1));
      assertTrue(comparator.equals(s1, "a"));
      assertFalse(comparator.equals(s1, s2));

      assertTrue(comparator.equals(i1, i1));
      assertTrue(comparator.equals(i1, 1));
      assertFalse(comparator.equals(i1, i2));
      
      try {
         assertFalse(comparator.equals(s1, i1));
         fail("A ClassCastException should be thrown");
      } catch (ClassCastException cce) {
         // expected
      }

      assertEquals(-i2.compareTo(i1), adaptee.compare(i2, i1));
      assertEquals(comparator.compare(i2, i1), adaptee.compare(i2, i1));
   }
}
