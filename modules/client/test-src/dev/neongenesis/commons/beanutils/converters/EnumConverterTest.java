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

import java.util.Collections;
import dev.neongenesis.GenesisTestCase;
import org.apache.commons.beanutils.ConversionException;

public class EnumConverterTest extends GenesisTestCase {
   public enum SampleEnum {
      A_NAME,
      B_NAME;

      public static final String A_STR = "A_NAME";
      public static final String B_STR = "B_NAME";
      public static final SampleEnum A = A_NAME;
      public static final SampleEnum B = B_NAME;
   }

   private EnumConverter nonNullConverter;
   private EnumConverter nullConverter;
   
   protected void setUp() throws Exception {
      nonNullConverter = new EnumConverter();
      nullConverter = new EnumConverter(true);
   }
   

   public void testNullReturn() {
      assertFalse(nonNullConverter.isNullReturnAcceptable());
      assertTrue(nullConverter.isNullReturnAcceptable());

      try {
         nonNullConverter.convert(SampleEnum.class, "AnyString");
         fail(nonNullConverter + " should throw a NPE for null");
      } catch (ConversionException ce) {
         // ok, worked
      }

      assertNull(nullConverter.convert(SampleEnum.class, "AnyString"));
   }

   public void testValidValues() {
      assertSame(SampleEnum.A, nonNullConverter.convert(SampleEnum.class, 
            SampleEnum.A_STR));
      assertSame(SampleEnum.A, nullConverter.convert(SampleEnum.class, 
            SampleEnum.A_STR));
      assertSame(SampleEnum.B, nonNullConverter.convert(SampleEnum.class, 
            SampleEnum.B_STR));
      assertSame(SampleEnum.B, nullConverter.convert(SampleEnum.class, 
            SampleEnum.B_STR));
   }

   public void testNullValue() {
      try {
         nonNullConverter.convert(SampleEnum.class, null);
         fail("nonNullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }

      assertNull(nullConverter.convert(SampleEnum.class, null));
   }

   public void testInvalidEnumClass() {
      try {
         nonNullConverter.convert(Integer.class, SampleEnum.A_STR);
         fail("nonNullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }

      try {
         nullConverter.convert(Integer.class, SampleEnum.A_STR);
         fail("nullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }
   }

   public void testInvalidValue() {
      try {
         nonNullConverter.convert(SampleEnum.class, Collections.EMPTY_LIST);
         fail("nonNullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }

      try {
         nullConverter.convert(SampleEnum.class, Collections.EMPTY_LIST);
         fail("nullConverter should throw a ConversionException");
      } catch (ConversionException ce) {
         // ok, worked
      }
   }

   public void testEnums() {
      assertSame(SampleEnum.A, nonNullConverter.convert(SampleEnum.class, 
            SampleEnum.A));
      assertSame(SampleEnum.A, nullConverter.convert(SampleEnum.class, 
            SampleEnum.A));
      
      Class<?> enumClass = Enum.class;

      assertSame(SampleEnum.A, nonNullConverter.convert(enumClass, 
            SampleEnum.A));
      assertSame(SampleEnum.A, nullConverter.convert(enumClass, SampleEnum.A));
   }
}
