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
package org.hibernate.criterion;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;

public class Order implements Serializable {
   private static final long serialVersionUID = 1L;

   private final String propertyName;
   private final boolean ascending;

   protected Order(final String propertyName, final boolean ascending) {
      this.propertyName = propertyName;
      this.ascending = ascending;
   }

   public static Order asc(final String propertyName) {
      return new Order(propertyName, true);
   }

   public static Order desc(final String propertyName) {
      return new Order(propertyName, false);
   }

   public String getPropertyName() {
      return propertyName;
   }

   public boolean isAscending() {
      return ascending;
   }

   public jakarta.persistence.criteria.Order toJpaOrder(
         final Root<?> root, final CriteriaBuilder cb) {
      final Path<?> path = Restrictions.resolvePath(root, propertyName);
      return ascending ? cb.asc(path) : cb.desc(path);
   }

   @Override
   public String toString() {
      return propertyName + ' ' + (ascending ? "asc" : "desc");
   }
}
