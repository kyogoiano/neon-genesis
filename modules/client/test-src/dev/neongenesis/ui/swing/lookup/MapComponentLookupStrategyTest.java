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
package dev.neongenesis.ui.swing.lookup;


import javax.swing.JPanel;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.ui.binding.MapLookupStrategy;

public class MapComponentLookupStrategyTest extends GenesisTestCase {
   private MapLookupStrategy strategy;
   private JPanel panel;
   private JPanel anotherPanel;

   public MapComponentLookupStrategyTest() {
      super("Map Component LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new MapComponentLookupStrategy();
      panel = new JPanel();
      anotherPanel = new JPanel();
   }

   public void testAll() {
      strategy.register("someName", panel);

      assertSame(panel, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(panel));

      assertNull(strategy.lookup(null, "none"));
      assertNull(strategy.getName(anotherPanel));
      
      Exception ex = null;
      try {
         strategy.register("someOtherName", panel);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(panel, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertNull(strategy.lookup(null, "someOtherName"));

      strategy.register("someName", anotherPanel);
      assertSame(anotherPanel, strategy.lookup(null, "someName"));
      assertEquals("someName", strategy.getName(anotherPanel));
      assertNull(strategy.getName(panel));

      assertNull(strategy.getName(new Object()));
      assertNull(strategy.getName(null));
   }
}
