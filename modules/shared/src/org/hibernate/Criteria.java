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
package org.hibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.query.Query;

/**
 * JPA Criteria-backed compatibility bridge for Genesis Criteria commands on Hibernate 6/7+.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Criteria {
   private final Session session;
   private final Class persistentClass;
   private final CriteriaBuilder criteriaBuilder;
   private final CriteriaQuery criteriaQuery;
   private final Root root;
   private final List<Predicate> predicates = new ArrayList<Predicate>();
   private final List<jakarta.persistence.criteria.Order> orders =
         new ArrayList<jakarta.persistence.criteria.Order>();
   private Integer firstResult;
   private Integer maxResults;

   public Criteria(final Session session, final Class persistentClass) {
      if (session == null) {
         throw new IllegalArgumentException("Session must not be null");
      }
      if (persistentClass == null) {
         throw new IllegalArgumentException("Persistent class must not be null");
      }
      this.session = session;
      this.persistentClass = persistentClass;
      this.criteriaBuilder = session.getCriteriaBuilder();
      this.criteriaQuery = criteriaBuilder.createQuery(persistentClass);
      this.root = criteriaQuery.from(persistentClass);
      this.criteriaQuery.select(root);
   }

   public static Criteria forSession(final Session session, final Class persistentClass) {
      return new Criteria(session, persistentClass);
   }

   public Session getSession() {
      return session;
   }

   public Class getPersistentClass() {
      return persistentClass;
   }

   public CriteriaBuilder getCriteriaBuilder() {
      return criteriaBuilder;
   }

   public CriteriaQuery getCriteriaQuery() {
      return criteriaQuery;
   }

   public Root getRoot() {
      return root;
   }

   public Criteria add(final Criterion criterion) {
      if (criterion != null) {
         final Predicate predicate = criterion.toPredicate(root, criteriaQuery, criteriaBuilder);
         if (predicate != null) {
            predicates.add(predicate);
         }
      }
      return this;
   }

   public Criteria add(final Predicate predicate) {
      if (predicate != null) {
         predicates.add(predicate);
      }
      return this;
   }

   public Criteria addOrder(final Order order) {
      if (order != null) {
         orders.add(order.toJpaOrder(root, criteriaBuilder));
      }
      return this;
   }

   public Criteria addOrder(final jakarta.persistence.criteria.Order order) {
      if (order != null) {
         orders.add(order);
      }
      return this;
   }

   public Criteria setFirstResult(final int firstResult) {
      this.firstResult = firstResult;
      return this;
   }

   public Criteria setMaxResults(final int maxResults) {
      this.maxResults = maxResults;
      return this;
   }

   private Query buildQuery() {
      criteriaQuery.select(root);
      if (!predicates.isEmpty()) {
         criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
      } else {
         criteriaQuery.where(new Predicate[0]);
      }
      if (!orders.isEmpty()) {
         criteriaQuery.orderBy(orders);
      } else {
         criteriaQuery.orderBy(Collections.emptyList());
      }
      final Query query = session.createQuery(criteriaQuery);
      if (firstResult != null) {
         query.setFirstResult(firstResult);
      }
      if (maxResults != null) {
         query.setMaxResults(maxResults);
      }
      return query;
   }

   public List list() throws HibernateException {
      return buildQuery().list();
   }

   public Object uniqueResult() throws HibernateException {
      return buildQuery().uniqueResult();
   }
}
