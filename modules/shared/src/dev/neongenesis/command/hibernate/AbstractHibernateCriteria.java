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
package dev.neongenesis.command.hibernate;

import dev.neongenesis.paging.Page;
import dev.neongenesis.paging.PagingException;
import dev.neongenesis.paging.hibernate.CriteriaPager;
import org.hibernate.Criteria;

public class AbstractHibernateCriteria extends AbstractHibernateCommand
      implements HibernateCriteria {
   private Criteria criteria;
   private int resultsPerPage;

   public AbstractHibernateCriteria() {
      this(10);
   }

   public AbstractHibernateCriteria(final int resultsPerPage) {
      this.resultsPerPage = resultsPerPage;
   }

   public void setCriteria(final Criteria criteria) {
      this.criteria = criteria;
   }

   protected Criteria getCriteria() {
      return criteria;
   }

   protected int getResultsPerPage() {
      return resultsPerPage;
   }

   protected void setResultsPerPage(int resultsPerPage) {
      this.resultsPerPage = resultsPerPage;
   }

   protected Page getPage(final int pageNumber) throws PagingException {
      return getPage(pageNumber, resultsPerPage);
   }

   protected Page getPage(final int pageNumber, final int resultsPerPage)
         throws PagingException {
      return new CriteriaPager(criteria).getPage(pageNumber, resultsPerPage);
   }
}