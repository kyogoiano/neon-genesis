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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import dev.neongenesis.GenesisTestCase;

public class CollectionEmptyResolverTest extends GenesisTestCase {
   public void testIsEmpty() {
      CollectionEmptyResolver resolver = new CollectionEmptyResolver();

      // Test for null
      assertTrue(resolver.isEmpty(null));

      // Test for non-empty collections
      assertFalse(resolver.isEmpty(fill(new ArrayList())));
      assertFalse(resolver.isEmpty(fill(new HashSet())));

      // Test for empty collections
      assertTrue(resolver.isEmpty(new ArrayList()));
      assertTrue(resolver.isEmpty(new HashSet()));
   }

   private Collection fill(Collection c) {
      c.add(new Object());

      return c;
   }
}