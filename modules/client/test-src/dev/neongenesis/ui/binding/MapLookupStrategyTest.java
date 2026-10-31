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
package dev.neongenesis.ui.binding;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.ui.binding.MapLookupStrategy;

public class MapLookupStrategyTest extends GenesisTestCase {
   private MapLookupStrategy strategy;
   private Object object1;
   private Object object2;

   public MapLookupStrategyTest() {
      super("Map LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new MapLookupStrategy() {};
      object1 = new Object();
      object2 = new Object();
   }

   public void testAll() {
      strategy.register("someName", object1);

      assertSame(object1, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(object1));

      assertNull(strategy.lookup(null, "none"));
      assertNull(strategy.getName(object2));
      
      Exception ex = null;
      try {
         strategy.register("someOtherName", object1);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(object1, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(object1));
      assertNull(strategy.lookup(null, "someOtherName"));

      strategy.register("someName", object2);
      assertSame(object2, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(object2));
      assertNull(strategy.getName(object1));
   }
}
