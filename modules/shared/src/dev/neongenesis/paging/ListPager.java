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
package dev.neongenesis.paging;

import java.util.ArrayList;
import java.util.List;
import dev.neongenesis.util.Bundle;

public class ListPager implements Pager {
   private final List list;
   private final boolean cloneList;

   public ListPager(List list) {
      this(list, true);
   }

   public ListPager(List list, boolean cloneList) {
      if (list == null) {
         throw new IllegalArgumentException(Bundle.getMessage(ListPager.class,
               "LIST_CANNOT_BE_NULL")); // NOI18N
      }

      this.list = list;
      this.cloneList = cloneList;
   }

   public Page getPage(int pageNumber, int resultsPerPage) 
         throws PagingException {
      final int listSize = list.size();

      int initialPosition = pageNumber * resultsPerPage;

      if (initialPosition >= listSize) {
         pageNumber = listSize / resultsPerPage;

         if (listSize % resultsPerPage == 0) {
            pageNumber = Math.max(0, pageNumber - 1);
         }

         initialPosition = pageNumber * resultsPerPage;
      }

      int finalPosition = Math.min(listSize, initialPosition + resultsPerPage);

      return new PageImpl(getListSection(initialPosition, finalPosition), 
            pageNumber, resultsPerPage, finalPosition == listSize);
   }

   protected List getListSection(int initialPosition, int finalPosition) {
      final List subList = list.subList(initialPosition, finalPosition);

      return (cloneList) ? new ArrayList(subList) : subList;
   }
}
