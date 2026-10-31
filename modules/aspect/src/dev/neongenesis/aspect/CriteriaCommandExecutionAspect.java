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
package dev.neongenesis.aspect;

import java.lang.reflect.Method;
import java.io.Serializable;
import java.util.Map;

import dev.neongenesis.annotation.Criteria;
import dev.neongenesis.command.CommandExecutionGuard;
import dev.neongenesis.command.hibernate.HibernateCriteria;
import dev.neongenesis.command.hibernate.CriteriaCommandExecutor;
import dev.neongenesis.command.hibernate.CriteriaResolver;

import dev.neongenesis.util.Bundle;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareMixin;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class CriteriaCommandExecutionAspect {
   private ThreadLocal threadLocal = new InheritableThreadLocal();

   private boolean preventStackOverflow() {
      return Boolean.getBoolean("genesis.aspect.preventCriteriaStackOverflow");
   }

   @Around("execution(@dev.neongenesis.annotation.Criteria * *(..))"
         + " && this(dev.neongenesis.command.hibernate.HibernateCriteria+)")
   public Object commandExecution(ProceedingJoinPoint joinPoint) throws Throwable {
      if (CommandExecutionGuard.isExecuting()) {
         return joinPoint.proceed();
      }

      Method method = ((MethodSignature)joinPoint.getSignature()).getMethod();
      boolean preventStackOverflow = preventStackOverflow();

      if (preventStackOverflow) {
         Method m = (Method)threadLocal.get();
         if (m != null && m.equals(method)) {
            Object value = joinPoint.proceed();
            threadLocal.set(null);
            return value;
         }
      }

      final CriteriaResolver obj = (CriteriaResolver)joinPoint.getTarget();
      final Class[] classes = method.getParameterTypes();
      final String[] classNames = new String[classes.length];

      for (int i = 0; i < classes.length; i++) {
         classNames[i] = classes[i].getName();
      }

      final String methodName = method.getName();
      final Object[] parameterValues = joinPoint.getArgs();

      final Criteria annon = method.getAnnotation(Criteria.class);
      
      final CriteriaAnnotationParser parser = new CriteriaAnnotationParser(annon);
      
      if (preventStackOverflow) {
         threadLocal.set(method);
      }

      try {
         return new CriteriaCommandExecutor(obj, methodName, classNames,
               parameterValues, parser.getPersisterClassName(), parser
                     .getOrderBy(), parser.isAsc(), obj.getPropertiesMap())
               .execute();
      } finally {
         if (preventStackOverflow) {
            threadLocal.set(null);
         }
      }
   }

   public static class CriteriaAnnotationParser {
      private String persisterClassName;
      private String[] orderBy;
      private boolean[] isAsc;

      public CriteriaAnnotationParser(Criteria annotation) {
         Class klazz = annotation.value();
         if (klazz != null && !Object.class.equals(annotation.value())) {
            persisterClassName = annotation.value().getName();
         }
         
         String[] values = annotation.orderby();
         orderBy = new String[values.length];
         isAsc = new boolean[values.length];
         for (int i = 0; i < values.length; i++) {
            String[] props = values[i].split("\\s+"); // NOI18N

            if (props.length == 0 || props.length > 2) {
               throw new IllegalArgumentException(
                     Bundle.getMessage(CriteriaCommandExecutionAspect.class,
                     "MALFORMED_CRITERIA_ANNOTATION")); // NOI18N
            }
            orderBy[i] = props[0];

            if (props.length < 2) {
               isAsc[i] = true;
               continue;
            }

            if (props[1].equalsIgnoreCase("asc")) { // NOI18N
               isAsc[i] = true;
            } else if (props[1].equalsIgnoreCase("desc")) { // NOI18N
               isAsc[i] = false;
            } else {
               throw new IllegalArgumentException(
                     Bundle.getMessage(CriteriaCommandExecutionAspect.class,
                     "MALFORMED_CRITERIA_ANNOTATION_ORDER_BY_CLAUSE_MUST_BE_ASC_OR_DESC")); // NOI18N
            }

         }
      }

      public String getPersisterClassName() {
         return persisterClassName;
      }

      public boolean[] isAsc() {
         return isAsc;
      }

      public String[] getOrderBy() {
         return orderBy;
      }
   }

   @DeclareMixin("dev.neongenesis.command.hibernate.HibernateCriteria+")
   public static CriteriaResolver createCriteriaResolver(HibernateCriteria target) {
      return new CriteriaResolverImpl();
   }

   public static class CriteriaResolverImpl
         implements CriteriaResolver, Serializable {
      private static final long serialVersionUID = 1L;
      private Map propertiesMap;

      public Map getPropertiesMap() {
         return propertiesMap;
      }

      public void setPropertiesMap(Map propertiesMap) {
         this.propertiesMap = propertiesMap;
      }
   }   
}