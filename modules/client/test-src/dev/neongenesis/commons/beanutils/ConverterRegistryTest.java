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
package dev.neongenesis.commons.beanutils;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.commons.beanutils.converters.BigDecimalConverter;
import dev.neongenesis.commons.beanutils.converters.DefaultConverter;
import dev.neongenesis.commons.beanutils.converters.EnumConverter;

import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.ArrayConverter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FileConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.commons.beanutils.converters.URLConverter;

public class ConverterRegistryTest extends GenesisTestCase {
   private ConverterRegistry converter;

   public void setUp() {
      converter = new ConverterRegistry();
   }
   
   public void testLookup() {
      assertConverter(BigInteger.class, BigIntegerConverter.class);
      assertConverter(Boolean.TYPE, BooleanConverter.class);
      assertConverter(Boolean.class, BooleanConverter.class);
      assertConverter(boolean[].class, ArrayConverter.class);
      assertConverter(Byte.TYPE, ByteConverter.class);
      assertConverter(Byte.class, ByteConverter.class);
      assertConverter(byte[].class, ArrayConverter.class);
      assertConverter(Character.TYPE, CharacterConverter.class);
      assertConverter(Character.class, CharacterConverter.class);
      assertConverter(char[].class, ArrayConverter.class);
      assertConverter(Class.class, ClassConverter.class);
      assertConverter(Double.TYPE, DoubleConverter.class);
      assertConverter(Double.class, DoubleConverter.class);
      assertConverter(double[].class, ArrayConverter.class);
      assertConverter(Float.TYPE, FloatConverter.class);
      assertConverter(Float.class, FloatConverter.class);
      assertConverter(float[].class, ArrayConverter.class);
      assertConverter(Integer.TYPE, IntegerConverter.class);
      assertConverter(Integer.class, IntegerConverter.class);
      assertConverter(int[].class, ArrayConverter.class);
      assertConverter(Long.TYPE, LongConverter.class);
      assertConverter(Long.class, LongConverter.class);
      assertConverter(long[].class, ArrayConverter.class);
      assertConverter(Short.TYPE, ShortConverter.class);
      assertConverter(Short.class, ShortConverter.class);
      assertConverter(short[].class, ArrayConverter.class);
      assertConverter(String.class, StringConverter.class);
      assertConverter(String[].class, ArrayConverter.class);
      assertConverter(Date.class, SqlDateConverter.class);
      assertConverter(Time.class, SqlTimeConverter.class);
      assertConverter(Timestamp.class, SqlTimestampConverter.class);
      assertConverter(File.class, FileConverter.class);
      assertConverter(URL.class, URLConverter.class);
      assertConverter(BigDecimal.class, BigDecimalConverter.class);
      assertConverter(Enum.class, EnumConverter.class);
      assertConverter(Object.class, DefaultConverter.class);
   }

   private void assertConverter(Class lookupClass, Class converterClass) {
      Object converterInstance = converter.lookup(lookupClass);
      assertNotNull(converterInstance);
      assertEquals(converterClass, converterInstance.getClass());
   }
   
   public void testDeregisterWithArg() {
      testDeregisterWithArg(Object.class);
      testDeregisterWithArg(BigInteger.class);
      testDeregisterWithArg(Boolean.TYPE);
      testDeregisterWithArg(Boolean.class);
   }

   private void testDeregisterWithArg(Class lookupClass) {
      assertNotNull(converter.lookup(lookupClass));
      converter.deregister(lookupClass);
      assertNull(converter.lookup(lookupClass));
   }
   
   public void testDeRegisterNoArg() {
      assertNotNull(converter.lookup(BigInteger.class));
      assertNotNull(converter.lookup(Boolean.TYPE));
      assertNotNull(converter.lookup(Boolean.class));

      converter.deregister();

      assertNull(converter.lookup(BigInteger.class));
      assertNull(converter.lookup(Boolean.TYPE));
      assertNull(converter.lookup(Boolean.class));
   }

   public void testRegister() {
      final Converter converterInstance = new DefaultConverter();

      //Test for non-registered type
      converter.register(converterInstance, Comparable.class);
      assertSame(converterInstance, converter.lookup(Comparable.class));

      //Test for already registered type
      converter.register(converterInstance, Integer.class);
      assertSame(converterInstance, converter.lookup(Integer.class));
   }
}