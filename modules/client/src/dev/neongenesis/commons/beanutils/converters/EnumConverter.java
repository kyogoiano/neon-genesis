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

import dev.neongenesis.helpers.EnumHelper;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

public class EnumConverter implements Converter {
   private final boolean nullReturnAcceptable;
   
   public EnumConverter() {
       this(false);
   }
   
   public EnumConverter(final boolean nullReturnAcceptable) {
       this.nullReturnAcceptable = nullReturnAcceptable;
   }
   
   public final boolean isNullReturnAcceptable() {
       return nullReturnAcceptable;
   }
   
   public Object convert(final Class type, Object value) {
       if (nullReturnAcceptable && value == null) {
           return null;
       }

       if (value != null && type.isAssignableFrom(value.getClass())) {
          return value;
       }

       Object en = null;
       Exception e = null;
       
       try {
           en = EnumHelper.getInstance().valueOf(type, (String) value);

           if (en == null && !nullReturnAcceptable) {
               throw new NullPointerException();
           }
       } catch(final ClassCastException cce) {
           e = cce;
       } catch(final IllegalArgumentException iae) {
           e = iae;
       } catch(final NullPointerException npe) {
           e = npe;
       }
       
       if (e != null) {
           throw new ConversionException(e);
       }

       return en;
   }
}

