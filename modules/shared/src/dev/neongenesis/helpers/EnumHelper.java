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
package dev.neongenesis.helpers;

import java.util.Arrays;
import java.util.Collection;

import dev.neongenesis.util.Bundle;

public final class EnumHelper {
   private static final EnumHelper instance = new EnumHelper();

   private EnumHelper() {
   }

   public static EnumHelper getInstance() {
      return instance;
   }

   public Class<?> getEnumClass() {
      return Enum.class;
   }

   public boolean supportsEnum() {
      return true;
   }

   public boolean isJava5Enum(Class<?> clazz) {
      return clazz != null && (clazz.isEnum() || Enum.class.isAssignableFrom(clazz));
   }

   public boolean isJava5Enum(Object object) {
      return object instanceof Enum<?>;
   }

   public boolean isEnum(Object object) {
      return object instanceof Enum<?>;
   }

   public Collection<?> values(Class<?> clazz) {
      if (clazz != null && clazz.isEnum()) {
         return Arrays.asList(clazz.getEnumConstants());
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", clazz == null ? "null" : clazz.getName())); // NOI18N
   }

   public Object valueOf(Class<?> clazz, String name) {
      if (clazz != null && clazz.isEnum()) {
         if (name == null) {
            return null;
         }
         Object[] enums = clazz.getEnumConstants();
         for (Object constant : enums) {
            if (name.equals(((Enum<?>) constant).name()) || name.equals(constant.toString())) {
               return constant;
            }
         }
         return null;
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", clazz == null ? "null" : clazz.getName())); // NOI18N
   }

   public Class<?> getDeclaringClass(Object object) {
      if (object instanceof Enum<?>) {
         return ((Enum<?>) object).getDeclaringClass();
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", object)); // NOI18N
   }

   public String getName(Object object) {
      if (object instanceof Enum<?>) {
         return ((Enum<?>) object).name();
      }

      throw new IllegalArgumentException(Bundle.getMessage(EnumHelper.class,
            "X_IS_NOT_AN_ENUM", object)); // NOI18N
   }
}

