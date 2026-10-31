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
package dev.neongenesis.commons.beanutils.converters;

import dev.neongenesis.util.Bundle;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class DefaultConverter implements Converter {
   private final boolean returnDefaultValue;
   private final Object defaultValue;

   public DefaultConverter() {
      this(true, null);
   }

   public DefaultConverter(final Object defaultValue) {
      this(true, defaultValue);
   }

   public DefaultConverter(final boolean returnDefaultValue) {
      this(returnDefaultValue, null);
   }

   protected DefaultConverter(final boolean returnDefaultValue,
         final Object defaultValue) {
      this.returnDefaultValue = returnDefaultValue;
      this.defaultValue = defaultValue;
   }

   public Object convert(Class clazz, Object obj) {
      if (obj == null) {
         return null;
      }

      if (clazz.isAssignableFrom(obj.getClass())) {
         return obj;
      }

      if (returnDefaultValue) {
         return defaultValue;
      }
      throw new ConversionException(Bundle.getMessage(DefaultConverter.class,
               "X_CANNOT_BE_CONVERTED_TO_Y_ITS_TYPE_IS_Z", new Object[]{ // NOI18N
            obj, clazz.getName(), obj.getClass().getName()}));
   }
}