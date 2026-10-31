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

import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import dev.neongenesis.GenesisTestCase;

public class DefaultJTableIndexResolverTest extends GenesisTestCase {
   private JComponent unsortableTable;
   private MockSortableTable sortableTable;
   private JTableIndexResolver defaultIndexResolver;

   public DefaultJTableIndexResolverTest() {
      super("DefaultJTableIndexResolver Unit Test");
   }

   protected void setUp() throws Exception {
      unsortableTable = new JComponent() {
      };
      sortableTable = new MockSortableTable();

      defaultIndexResolver = new DefaultJTableIndexResolver();
   }

   public void testTableIndexResolver() throws Exception {
      assertFalse(defaultIndexResolver.needsConversion(unsortableTable));
      assertTrue(defaultIndexResolver.needsConversion(sortableTable));

      try {
         assertEquals(0, defaultIndexResolver.convertRowIndexToView(
               unsortableTable, 0));
         fail("Shouldn't be possible to convert a index when needsConversion is false");
      } catch (NullPointerException npe) {
      }

      try {
         assertEquals(0, defaultIndexResolver.convertRowIndexToModel(
               unsortableTable, 0));
         fail("Shouldn't be possible to convert a index when needsConversion is false");
      } catch (NullPointerException npe) {
      }

      Map map = new HashMap();
      map.put(1, 3);
      map.put(2, 2);
      map.put(3, 4);
      map.put(4, 0);
      map.put(5, 1);

      for (int i = 1; i <= 5; i++) {
         int modelIndex = (Integer) map.get(i);
         int viewIndex = modelIndex;

         validateIndexes(modelIndex, viewIndex);

         assertEquals(defaultIndexResolver.convertRowIndexToModel(sortableTable,
               viewIndex), defaultIndexResolver.convertRowIndexToView(
               sortableTable, modelIndex));
      }

      sortableTable.sort();

      for (int i = 1; i <= 5; i++) {
         int modelIndex = (Integer) map.get(i);
         int viewIndex = i - 1;

         validateIndexes(modelIndex, viewIndex);
      }
   }

   private void validateIndexes(int modelIndex, int viewIndex) throws Exception {
      assertEquals(modelIndex, defaultIndexResolver.convertRowIndexToModel(
            sortableTable, viewIndex));
      assertEquals(viewIndex, defaultIndexResolver.convertRowIndexToView(
            sortableTable, modelIndex));
   }
}
