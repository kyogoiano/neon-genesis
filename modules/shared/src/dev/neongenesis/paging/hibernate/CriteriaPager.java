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
package dev.neongenesis.paging.hibernate;

import java.util.List;

import dev.neongenesis.paging.Page;
import dev.neongenesis.paging.PageImpl;
import dev.neongenesis.paging.Pager;
import dev.neongenesis.paging.PagingException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;


public class CriteriaPager implements Pager {

   private final Criteria crit;

   public CriteriaPager(final Criteria crit) {
      this.crit = crit;
   }

   public Page getPage(final int pageNumber, final int resultsPerPage)
         throws PagingException {
      return getPage(pageNumber, resultsPerPage, false);
   }

   public Page getPage(int pageNumber, final int resultsPerPage,
         boolean last) throws PagingException {
      if (pageNumber < 0) {
         pageNumber = 0;
      }

     try {
         crit.setFirstResult(pageNumber * resultsPerPage);
         crit.setMaxResults(resultsPerPage);
         final List results = crit.list();
         if (pageNumber > 0 && results.isEmpty()) {
            return getPage(pageNumber - 1, resultsPerPage, true);
         }
         return new PageImpl(results, pageNumber, resultsPerPage, last || 
               results.size() < resultsPerPage);
      } catch (HibernateException e) {
         throw new PagingException(e);
      }
   }

}