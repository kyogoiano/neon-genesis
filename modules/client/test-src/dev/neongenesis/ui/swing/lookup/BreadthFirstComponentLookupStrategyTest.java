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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import dev.neongenesis.GenesisTestCase;

public class BreadthFirstComponentLookupStrategyTest extends GenesisTestCase {
   private BreadthFirstComponentLookupStrategy strategy;
   private JRootPane root;
   private JPanel rootLevel1Position0;
   private JPanel rootLevel1Position1;
   private JPanel rootLevel2Position0;
   private JPanel panelLevel1Position1;
   private JPanel panelLevel2Position0;
   private JMenuBar menuBar;
   private JMenu menu;
   private JMenuItem menuItem;

   public BreadthFirstComponentLookupStrategyTest() {
      super("Breadth First Component LookupStrategy Unit Test");
   }

   protected void setUp() {
      strategy = new BreadthFirstComponentLookupStrategy();
      panelLevel1Position1 = new JPanel();
      panelLevel2Position0 = new JPanel();
      root = new JRootPane();
      rootLevel1Position0 = new JPanel();
      rootLevel1Position1 = new JPanel();
      rootLevel2Position0 = new JPanel();

      root.getContentPane().add(rootLevel1Position0);
      root.getContentPane().add(rootLevel1Position1);
      rootLevel1Position0.add(rootLevel2Position0);

      rootLevel1Position1.add(panelLevel1Position1);
      rootLevel2Position0.add(panelLevel2Position0);

      menuBar = new JMenuBar();

      menu = new JMenu();
      menu.setName("aMenu");
      menuBar.add(menu);

      menuItem = new JMenuItem();
      menuItem.setName("aMenuItem");
      menu.add(menuItem);
   }

   public void testSimpleLookup() {
      panelLevel1Position1.setName("someName");

      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));
      
      panelLevel1Position1.setName("anotherName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "anotherName"));
      
      panelLevel2Position0.setName("theName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "theName"));
   }
   
   public void testBreadthFirstLookup() {
      panelLevel1Position1.setName("someName");
      panelLevel2Position0.setName("someName");
      assertSame(panelLevel1Position1, strategy.lookup(root, "someName"));
      
      panelLevel1Position1.setName("changeName");
      assertSame(panelLevel2Position0, strategy.lookup(root, "someName"));

      assertNull(strategy.lookup(root, "unknownName"));
   }

   public void testLookupForMenus() {
      assertSame(menu, strategy.lookup(menuBar, menu.getName()));
      assertSame(menuItem, strategy.lookup(menuBar, menuItem.getName()));
   }
   
   public void testRegisteredAlias() {
      strategy.register("menuAlias", menu);
      assertSame(menuItem, strategy.lookup(menuBar, menuItem.getName()));
   }
}
