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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import dev.neongenesis.hibernate.type.HibernateTypeValue;
import dev.neongenesis.paging.Page;
import dev.neongenesis.paging.PagingException;
import dev.neongenesis.paging.hibernate.CriteriaPager;
import dev.neongenesis.paging.hibernate.QueryPager;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.type.BindableType;
import org.hibernate.query.Query;

public abstract class AbstractHibernateCommand implements HibernateCommand {
   private Session session;

   public void setSession(Session session) {
      this.session = session;
   }

   protected Session getSession() {
      return session;
   }

   protected Criteria createCriteria(final Class persistentClass) {
      return Criteria.forSession(getSession(), persistentClass);
   }

   protected void appendClause(StringBuffer where, String condition) {
      if (where.length() != 0) {
         where.append(" and "); // NOI18N
      }

      where.append(condition);
   }

   protected Query generateQuery(String select, Map tables, String where,
         String orderBy, Map parameters, int maxResults)
         throws HibernateException {

      StringBuffer hql = new StringBuffer(select);

      hql.append(" from "); // NOI18N

      Map.Entry e;

      for (Iterator i = tables.entrySet().iterator(); i.hasNext();) {
         e = (Map.Entry) i.next();
         hql.append(e.getValue()).append(' ').append(e.getKey()).append(','); // NOI18N
      }

      hql.setLength(hql.length() - 1);

      if (where != null && where.length() != 0) {
         hql.append(" where ").append(where); // NOI18N
      }

      if (orderBy != null && orderBy.trim().length() > 0) {
         hql.append(" order by ").append(orderBy); // NOI18N
      }

      Query q = getSession().createQuery(hql.toString());

      for (Iterator i = parameters.entrySet().iterator(); i.hasNext();) {
         e = (Map.Entry) i.next();

	   String key = e.getKey().toString();
	   Object value = e.getValue();

	   Class valueClass = value.getClass();

         if (HibernateTypeValue.class.isAssignableFrom(valueClass)) {
            final HibernateTypeValue htv = (HibernateTypeValue)value;
            q.setParameter(key, htv.getValue(), (BindableType) htv
                        .getType());
	   } else if (Collection.class.isAssignableFrom(valueClass)) {
		q.setParameterList(key, (Collection)value);
	   } else if (valueClass.isArray()) {
		q.setParameterList(key, (Object[])value);
         } else {
            q.setParameter(key, value);
         }
      }

      if (maxResults > 0) {
         q.setMaxResults(maxResults);
      }

      return q;
   }

   protected Query generateQuery(String select, Map tables, String where,
         String orderBy, Map parameters) throws HibernateException {

      return generateQuery(select, tables, where, orderBy, parameters, 0);
   }

   protected Page getPage(final Criteria crit, final int pageNumber,
         final int resultsPerPage) throws PagingException {
      return new CriteriaPager(crit).getPage(pageNumber, resultsPerPage);
   }

   protected Page getPage(final Query query, final int pageNumber,
         final int resultsPerPage) throws PagingException {
      return new QueryPager(query).getPage(pageNumber, resultsPerPage);
   }

}