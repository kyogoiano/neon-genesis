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

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.reflection.ClassesCache;
import dev.neongenesis.reflection.ReflectionInvoker;
import dev.neongenesis.util.Bundle;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import org.apache.commons.beanutils.PropertyUtils;

public class CriteriaCommandExecutor extends AbstractHibernateCommand {
   private final Object realCommand;
   private final String methodName;
   private final String[] classNames;
   private final Object[] args;
   private final String persisterClassName;
   private final String[] orderBy;
   private final boolean[] isAsc;

   private final Map propertiesMap;

   public CriteriaCommandExecutor(Object realCommand, String methodName,
         String[] classNames, Object[] args, String persisterClassName,
         String[] orderBy, boolean[] isAsc,
         Map propertiesMap) {
      this.realCommand = realCommand;
      this.methodName = methodName;
      this.classNames = classNames;
      this.args = args;
      this.persisterClassName = persisterClassName;
      this.orderBy = orderBy;
      this.isAsc = isAsc;

      if (propertiesMap == null) {
         throw new IllegalArgumentException(Bundle.getMessage(
               CriteriaCommandExecutor.class, "NULL_PROPERTY_MAP_CRITERIA")); // NOI18N
      }

      this.propertiesMap = propertiesMap;
   }

   @Remotable
   public Object execute() throws Throwable {
      final HibernateCriteria hibCriteria = (HibernateCriteria) realCommand;

      if (persisterClassName != null && persisterClassName.trim().length() != 0) {
         final Criteria crit = createCriteria(
               ClassesCache.getClass(persisterClassName));
         
         hibCriteria.setCriteria(crit);
         
         if (orderBy != null) {
            for (int i = 0; i < orderBy.length; i++) {
               crit.addOrder(isAsc[i] ? Order.asc(orderBy[i]) : Order.desc(orderBy[i]));
            }
         }
         
      } else {
         hibCriteria.setCriteria(null);
      }

      PropertyUtils.copyProperties(hibCriteria, propertiesMap);

      try {
         return ReflectionInvoker.getInstance().invoke(realCommand, methodName,
               classNames, args);
      } catch (InvocationTargetException ite) {
         throw ite.getTargetException();
      }
   }

   public void setSession(Session session) {
      super.setSession(session);

      if (realCommand instanceof HibernateCommand) {
         ((HibernateCommand)realCommand).setSession(session);
      }
   }
}
