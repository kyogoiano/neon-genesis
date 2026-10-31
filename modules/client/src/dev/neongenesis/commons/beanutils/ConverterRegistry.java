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

import dev.neongenesis.commons.beanutils.converters.BigDecimalConverter;
import dev.neongenesis.commons.beanutils.converters.DefaultConverter;
import dev.neongenesis.commons.beanutils.converters.EnumConverter;
import dev.neongenesis.registry.Registry;

import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigIntegerConverter;
import org.apache.commons.beanutils.converters.BooleanArrayConverter;
import org.apache.commons.beanutils.converters.BooleanConverter;
import org.apache.commons.beanutils.converters.ByteArrayConverter;
import org.apache.commons.beanutils.converters.ByteConverter;
import org.apache.commons.beanutils.converters.CharacterArrayConverter;
import org.apache.commons.beanutils.converters.CharacterConverter;
import org.apache.commons.beanutils.converters.ClassConverter;
import org.apache.commons.beanutils.converters.DoubleArrayConverter;
import org.apache.commons.beanutils.converters.DoubleConverter;
import org.apache.commons.beanutils.converters.FileConverter;
import org.apache.commons.beanutils.converters.FloatArrayConverter;
import org.apache.commons.beanutils.converters.FloatConverter;
import org.apache.commons.beanutils.converters.IntegerArrayConverter;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.apache.commons.beanutils.converters.LongArrayConverter;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.beanutils.converters.ShortArrayConverter;
import org.apache.commons.beanutils.converters.ShortConverter;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.beanutils.converters.SqlTimeConverter;
import org.apache.commons.beanutils.converters.SqlTimestampConverter;
import org.apache.commons.beanutils.converters.StringArrayConverter;
import org.apache.commons.beanutils.converters.StringConverter;
import org.apache.commons.beanutils.converters.URLConverter;

public final class ConverterRegistry extends ConvertUtilsBean {
   private Registry registry;

   public ConverterRegistry() {
      getRegistry().register(BigInteger.class, new BigIntegerConverter());
      registry.register(Boolean.TYPE, new BooleanConverter(Boolean.FALSE));
      registry.register(Boolean.class, new BooleanConverter(null));
      registry.register(boolean[].class, new BooleanArrayConverter(
            new boolean[0]));
      registry.register(Byte.TYPE, new ByteConverter((byte) 0));
      registry.register(Byte.class, new ByteConverter(null));
      registry.register(byte[].class, new ByteArrayConverter(new byte[0]));
      registry.register(Character.TYPE, new CharacterConverter(' '));
      registry.register(Character.class, new CharacterConverter(null));
      registry.register(char[].class, new CharacterArrayConverter(new char[0]));
      registry.register(Class.class, new ClassConverter());
      registry.register(Double.TYPE, new DoubleConverter(0.0));
      registry.register(Double.class, new DoubleConverter(null));
      registry
            .register(double[].class, new DoubleArrayConverter(new double[0]));
      registry.register(Float.TYPE, new FloatConverter(0.0f));
      registry.register(Float.class, new FloatConverter(null));
      registry.register(float[].class, new FloatArrayConverter(new float[0]));
      registry.register(Integer.TYPE, new IntegerConverter(0));
      registry.register(Integer.class, new IntegerConverter(null));
      registry.register(int[].class, new IntegerArrayConverter(new int[0]));
      registry.register(Long.TYPE, new LongConverter(0L));
      registry.register(Long.class, new LongConverter(null));
      registry.register(long[].class, new LongArrayConverter(new long[0]));
      registry.register(Short.TYPE, new ShortConverter((short) 0));
      registry.register(Short.class, new ShortConverter(null));
      registry.register(short[].class, new ShortArrayConverter(new short[0]));
      registry.register(String.class, new StringConverter());
      registry
            .register(String[].class, new StringArrayConverter(new String[0]));
      registry.register(Date.class, new SqlDateConverter());
      registry.register(Time.class, new SqlTimeConverter());
      registry.register(Timestamp.class, new SqlTimestampConverter());
      registry.register(File.class, new FileConverter());
      registry.register(URL.class, new URLConverter());

      registry.register(BigDecimal.class, new BigDecimalConverter());
      registry.register(Enum.class, new EnumConverter());
      registry.register(Object.class, new DefaultConverter());
   }

   // To avoid NPE when ConvertUtilsBean constructor calls 
   // register(converter,class). The registry instance would be null at that 
   // time due to the initialization order of member variables
   private Registry getRegistry() {
      if (registry == null) {
         registry = new Registry();
      }
      return registry;
   }

   public void register(Converter converter, Class clazz) {
      getRegistry().register(clazz, converter);
   }

   public void deregister() {
      getRegistry().deregister();
   }

   public void deregister(Class clazz) {
      getRegistry().deregister(clazz);
   }

   public Converter lookup(Class clazz) {
      return (Converter)registry.get(clazz, true);
   }
}