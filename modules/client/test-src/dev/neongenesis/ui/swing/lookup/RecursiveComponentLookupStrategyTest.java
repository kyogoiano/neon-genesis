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

public class RecursiveComponentLookupStrategyTest extends GenesisTestCase {
   private MockRecursiveComponentLookupStrategy strategy;
   private JPanel root;
   private JPanel panel;
   private JPanel anotherPanel;

   public RecursiveComponentLookupStrategyTest() {
      super("Recursive Component LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new MockRecursiveComponentLookupStrategy();

      root = new JPanel();
      panel = new JPanel();
      anotherPanel = new JPanel();
   }

   public void testWithRegister() {
      strategy.register("someName", panel);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));

      assertSame(root, strategy.lookup(root, "none"));
      assertEquals("none", strategy.get("lookupImpl(Component,String)"));
      assertNull(strategy.getName(anotherPanel));
      
      Exception ex = null;
      try {
         strategy.register("someOtherName", panel);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertSame(root, strategy.lookup(root, "someOtherName"));
      assertEquals("someOtherName", strategy.get("lookupImpl(Component,String)"));

      strategy.register("someName", anotherPanel);
      assertSame(anotherPanel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(anotherPanel));
      assertNull(strategy.getName(panel));
   }
   
   public void testWithComponentName() {
      panel.setName("someName");

      // Mock object will return root. So we ensure that lookupImpl was called
      assertSame(root, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertEquals("someName", strategy.get("lookupImpl(Component,String)"));

      assertEquals(root, strategy.lookup(root, "none"));
      assertEquals("none", strategy.get("lookupImpl(Component,String)"));
      assertNull(strategy.getName(anotherPanel));
      
      strategy.register("someName", panel);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));

      Exception ex = null;
      try {
         strategy.register("someOtherName", panel);
      } catch(Exception e) {
         ex = e;
      }

      assertTrue(ex instanceof IllegalArgumentException);

      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(panel));
      assertSame(root, strategy.lookup(root, "someOtherName"));
      assertEquals("someOtherName", strategy.get("lookupImpl(Component,String)"));

      anotherPanel.setName("someName");
      assertSame(panel, strategy.lookup(root, "someName"));
      assertEquals("someName", strategy.getName(anotherPanel));
      assertEquals("someName", strategy.getName(panel));
   }

   public void testNoComponents() {
      assertNull(strategy.getRealName(null));
      assertNull(strategy.getRealName(new Object()));
      assertNull(strategy.lookup(null, null));
      assertNull(strategy.lookup(null, ""));
      assertNull(strategy.lookup(new Object(), null));
      assertNull(strategy.lookup(new Object(), ""));
   }

}
