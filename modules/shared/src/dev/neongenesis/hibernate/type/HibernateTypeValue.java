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
package dev.neongenesis.hibernate.type;

import org.hibernate.MappingException;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.util.Properties;

public class HibernateTypeValue {

   private Type type;
   private Object value;

   public HibernateTypeValue(Object value, Type type) {
      this.type = type;
      this.value = value;
   }

   public HibernateTypeValue(Object value, Class typeValue, Properties parameters)
         throws MappingException {
      this(value, createCustomType(typeValue, parameters));
   }

   private static Type createCustomType(Class typeValue, Properties parameters)
         throws MappingException {
      try {
         UserType userType = (UserType) typeValue.newInstance();
         if (userType instanceof ParameterizedType) {
            ((ParameterizedType) userType).setParameterValues(parameters);
         }
         return new CustomType(userType, new TypeConfiguration());
      } catch (InstantiationException e) {
         throw new MappingException("Could not instantiate user type " + typeValue, e);
      } catch (IllegalAccessException e) {
         throw new MappingException("Could not access user type " + typeValue, e);
      } catch (ClassCastException e) {
         throw new MappingException("Type does not implement UserType: " + typeValue, e);
      }
   }

   public Type getType() {
      return this.type;
   }

   public Object getValue() {
      return this.value;
   }
}