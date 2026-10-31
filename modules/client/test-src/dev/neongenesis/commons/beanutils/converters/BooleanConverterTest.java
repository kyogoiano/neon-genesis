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

public class BooleanConverterTest extends GenesisTestCase {
   private void testConverter(BooleanConverter converter,
         Object trueValue, Object falseValue, boolean returnDefaultValue, 
         boolean nullAsDefaultValue, Boolean defaultValue) {
      // First true and false
      assertEquals(Boolean.TRUE, converter.convert(Boolean.class, Boolean.TRUE));
      assertEquals(Boolean.FALSE, converter.convert(Boolean.class, Boolean.FALSE));

      // Then true and false values
      assertEquals(Boolean.TRUE, converter.convert(Boolean.class, trueValue));
      assertEquals(Boolean.FALSE, converter.convert(Boolean.class, falseValue));

      // Then null
      Object ret = converter.convert(Boolean.class, null);

      if (nullAsDefaultValue) {
         assertSame(defaultValue, ret);
      } else {
         assertNull(ret);
      }

      // Then an arbitrary value
      try {
         assertSame(defaultValue, converter.convert(Object.class, new Object()));

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
      testConverter(new BooleanConverter(), BooleanConverter.TRUE, 
            BooleanConverter.FALSE, true, false,  Boolean.FALSE);
   }

   public void testTwoObjectsConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();

      testConverter(new BooleanConverter(trueValue, falseValue), trueValue, 
            falseValue, true, false, Boolean.FALSE);
   }

   public void testTwoObjectsDefaultConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();
      Boolean defaultValue = Boolean.TRUE;

      testConverter(new BooleanConverter(trueValue, falseValue, defaultValue), 
            trueValue, falseValue, true, false, defaultValue);
   }

   public void testTwoObjectsFlagConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();

      testConverter(new BooleanConverter(trueValue, falseValue, true), 
            trueValue, falseValue, true, false, Boolean.FALSE);
      testConverter(new BooleanConverter(trueValue, falseValue, false), 
            trueValue, falseValue, false, false, null);
   }

   public void testFullConstructor() {
      Object trueValue = new Object();
      Object falseValue = new Object();
      Boolean defaultValue = Boolean.TRUE;

      testConverter(new BooleanConverter(trueValue, falseValue, false, false, 
            defaultValue), trueValue, falseValue, false, false, defaultValue);
      testConverter(new BooleanConverter(trueValue, falseValue, false, true, 
            defaultValue), trueValue, falseValue, false, true, defaultValue);
      testConverter(new BooleanConverter(trueValue, falseValue, true, false, 
            defaultValue), trueValue, falseValue, true, false, defaultValue);
      testConverter(new BooleanConverter(trueValue, falseValue, true, true, 
            defaultValue), trueValue, falseValue, true, true, defaultValue);
   }
}