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
package dev.neongenesis.ui.swing.components.table;

import javax.swing.JTable;
import dev.neongenesis.GenesisTestCase;

public class JTableIndexResolverRegistryTest extends GenesisTestCase {
   private class SubJTable extends JTable {
   }
   private JTableIndexResolverRegistry indexResolverRegistry;

   public JTableIndexResolverRegistryTest() {
      super("JTableIndexResolverRegistry Unit Test");
   }

   protected void setUp() throws Exception {
      indexResolverRegistry = JTableIndexResolverRegistry.getInstance();
   }

   public void testIndexResolverRegistry() {
      assertNull(indexResolverRegistry.get(MockSortableTable.class));

      JTableIndexResolver indexResolver;
      indexResolverRegistry.register(MockSortableTable.class,
            indexResolver = new DefaultJTableIndexResolver());

      assertNotNull(indexResolverRegistry.get(MockSortableTable.class));
      assertNotNull(indexResolverRegistry.get(JTable.class));
      assertNull(indexResolverRegistry.get(SubJTable.class));

      assertNotNull(indexResolverRegistry.get(new MockSortableTable()));
      assertNotNull(indexResolverRegistry.get(new JTable()));
      assertNotNull(indexResolverRegistry.get(new SubJTable()));

      assertNotNull(indexResolverRegistry.get(MockSortableTable.class, false));
      assertNotNull(indexResolverRegistry.get(JTable.class, false));
      assertNull(indexResolverRegistry.get(SubJTable.class, false));
      assertNotNull(indexResolverRegistry.get(MockSortableTable.class, true));
      assertNotNull(indexResolverRegistry.get(JTable.class, true));
      assertNotNull(indexResolverRegistry.get(SubJTable.class, true));

      assertSame(indexResolver, indexResolverRegistry.get(
            MockSortableTable.class));
      assertSame(indexResolver, indexResolverRegistry.get(
            new MockSortableTable()));
      assertSame(indexResolver, indexResolverRegistry.get(
            MockSortableTable.class, false));
      assertSame(indexResolver, indexResolverRegistry.get(
            MockSortableTable.class, true));

      final JTableIndexResolver tableIndexResolver = indexResolverRegistry.get(
            JTable.class);
      assertTrue(tableIndexResolver instanceof DefaultJTableIndexResolver);
      assertSame(tableIndexResolver, indexResolverRegistry.get(new JTable()));
      assertSame(tableIndexResolver, indexResolverRegistry.get(JTable.class,
            false));
      assertSame(tableIndexResolver, indexResolverRegistry.get(JTable.class,
            true));

      indexResolverRegistry.deregister(MockSortableTable.class);
      assertNull(indexResolverRegistry.get(MockSortableTable.class));

      indexResolverRegistry.register(MockSortableTable.class,
            new DefaultJTableIndexResolver());
      indexResolverRegistry.deregister();
      assertNull(indexResolverRegistry.get(MockSortableTable.class));
      assertNull(indexResolverRegistry.get(JTable.class));
   }
}
