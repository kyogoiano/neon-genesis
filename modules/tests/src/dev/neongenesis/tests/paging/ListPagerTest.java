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
package dev.neongenesis.tests.paging;

import java.util.ArrayList;
import java.util.List;

import dev.neongenesis.paging.ListPager;
import dev.neongenesis.paging.Page;
import dev.neongenesis.paging.PagingException;
import dev.neongenesis.tests.TestCase;

public class ListPagerTest extends TestCase {
   public void testIllegalConstructor() {
      try {
         new ListPager(null);
         fail("IllegalArgumentException should be thrown for null list");
      } catch (IllegalArgumentException iae) {
         // ok
      }
   }

   public void testCloneList() throws PagingException {
      testCloneList(true);
      testCloneList(false);
   }

   private void testCloneList(boolean cloneList) throws PagingException {
      int elements = 10;
      List list = createList(elements);

      ListPager pager = new ListPager(list, cloneList);
      pager.getPage(0, 1).getResults().clear();

      assertEquals(cloneList, elements == list.size());
   }

   private List createList(int elements) {
      List list = new ArrayList(elements);

      for (int i = 0; i < elements; i++) {
         list.add(i);
      }

      return list;
   }

   public void testGetPage() throws PagingException {
      int elements = 10;
      List list = createList(elements);
      ListPager pager = new ListPager(list);

      testGetPage(pager, 0, 9, 0, 9, true, false, 0);
      testGetPage(pager, 0, 10, 0, 10, true, true,0);
      testGetPage(pager, 1, 10, 0, 10, true, true,0);

      testGetPage(pager, 1, 5, 1, 5, false, true,5);
      testGetPage(pager, 2, 5, 1, 5, false, true,5);

      testGetPage(pager, 3, 3, 3, 1, false, true,9);
   }

   private void testGetPage(ListPager pager, int pageNumber, int resultsPerPage, 
         int expectedPageNumber, int expectedResultCount, boolean first, 
         boolean last, Integer firstValue) throws PagingException {
      Page page = pager.getPage(pageNumber, resultsPerPage);
      assertEquals(expectedPageNumber, page.getPageNumber());
      assertEquals(expectedResultCount, page.getResults().size());
      assertEquals(first, page.isFirst());
      assertEquals(last, page.isLast());
      assertEquals(firstValue, page.getResults().get(0));
   }
}