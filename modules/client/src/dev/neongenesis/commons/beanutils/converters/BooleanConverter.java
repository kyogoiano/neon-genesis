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

public class BooleanConverter implements Converter {
   public static final String TRUE = "true"; // NOI18N
   public static final String FALSE = "false"; // NOI18N

   private final Object trueValue;
   private final Object falseValue;
   private final boolean returnDefaultValue;
   private final boolean nullAsDefaultValue;
   private final Boolean defaultValue;

   public BooleanConverter() {
      this(TRUE, FALSE, true, false, Boolean.FALSE);
   }

   public BooleanConverter(Object trueValue, Object falseValue) {
      this(trueValue, falseValue, true, false, Boolean.FALSE);
   }

   public BooleanConverter(Object trueValue, Object falseValue, 
         Boolean defaultValue) {
      this(trueValue, falseValue, true, false, defaultValue);
   }

   public BooleanConverter(Object trueValue, Object falseValue, 
         boolean returnDefaultValue) {
      this(trueValue, falseValue, returnDefaultValue, false,
            returnDefaultValue ?  Boolean.FALSE : null);
   }

   public BooleanConverter(Object trueValue, Object falseValue, 
         boolean returnDefaultValue, boolean nullAsDefaultValue, 
         Boolean defaultValue) {
      this.trueValue = trueValue;
      this.falseValue = falseValue;
      this.returnDefaultValue = returnDefaultValue;
      this.nullAsDefaultValue = nullAsDefaultValue;
      this.defaultValue = defaultValue;
   }

   public Object convert(Class clazz, Object obj) {
      if (obj == null) {
         return nullAsDefaultValue ? defaultValue : null;
      }

      if (obj.getClass().equals(Boolean.class)) {
         return obj;
      }

      if (trueValue.equals(obj)) {
         return Boolean.TRUE;
      } else if (falseValue.equals(obj)) {
         return Boolean.FALSE;
      } else if (returnDefaultValue) {
         return defaultValue;
      } else {
         throw new ConversionException(Bundle.getMessage(BooleanConverter.class,
               "X_CANNOT_BE_CONVERTED_TO_Y_ITS_TYPE_IS_Z", new Object[]{ // NOI18N
            obj, clazz.getName(), obj.getClass().getName()}));
      }
   }
}