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

import dev.neongenesis.GenesisTestCase;
import org.apache.commons.beanutils.ConversionException;

public class DefaultConverterTest extends GenesisTestCase {
   private void testConverter(DefaultConverter converter,
         boolean returnDefaultValue, Object defaultValue) {
      assertNull(converter.convert(Object.class, null));

      Object value = new Object();
      assertEquals(converter.convert(Object.class, value), value);

      try {
         assertEquals(converter.convert(String.class, value), defaultValue);

         if (!returnDefaultValue) {
            fail("Should have thrown a ConversionException instead");
         }
      } catch (ConversionException ce) {
         if (returnDefaultValue) {
            fail("Should have returned the default value instead");
         }
      }
   }

   public void testDefaultConstructor() {
      testConverter(new DefaultConverter(), true, null);
   }

   public void testObjectConstructor() {
      Object value = new Object();
      testConverter(new DefaultConverter(value), true, value);
   }

   public void testBooleanConstructor() {
      testConverter(new DefaultConverter(true), true, null);
      testConverter(new DefaultConverter(false), false, null);
   }

   public void testBooleanObjectConstructor() {
      Object value = new Object();
      testConverter(new DefaultConverter(true, value), true, value);
      testConverter(new DefaultConverter(false, value), false, value);
   }
}