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
package dev.neongenesis.text;

import dev.neongenesis.GenesisTestCase;

public class FormatterRegistryTest extends GenesisTestCase {
   private enum EnumClass {
      INSTANCE
   }

   private static final String TEXT = "text";

   private final Formatter formatter = new Formatter() {
      public String format(Object o) {
         return TEXT;
      }
   };

   public void testGetClass() {
      assertNull(FormatterRegistry.getInstance().get(EnumClass.class));
      assertEquals(EnumFormatter.class.getName(), FormatterRegistry
            .getInstance().get(Enum.class).getClass().getName());

      assertNull(FormatterRegistry.getInstance().get(String.class));
      assertNull(FormatterRegistry.getInstance().get(Comparable.class));
      assertEquals(DefaultFormatter.class.getName(), FormatterRegistry
            .getInstance().get(Object.class).getClass().getName());
   }

   public void testGetClassBoolean() {
      testGetClassBoolean(EnumFormatter.class, EnumClass.class, true);
      testGetClassBoolean(null, EnumClass.class, false);

      testGetClassBoolean(EnumFormatter.class, Enum.class, true);
      testGetClassBoolean(EnumFormatter.class, Enum.class, false);

      testGetClassBoolean(DefaultFormatter.class, String.class, true);
      testGetClassBoolean(null, String.class, false);

      testGetClassBoolean(DefaultFormatter.class, Comparable.class, true);
      testGetClassBoolean(null, Comparable.class, false);

      testGetClassBoolean(DefaultFormatter.class, Object.class, true);
      testGetClassBoolean(DefaultFormatter.class, Object.class, false);
   }

   private void testGetClassBoolean(Class formatterClass, Class lookupClass,
         boolean superClass) {
      final Object ret = FormatterRegistry.getInstance().get(lookupClass,
            superClass);
      if (formatterClass == null) {
         assertNull(ret);
      } else {
         assertEquals(formatterClass.getName(), ret.getClass().getName());
      }
   }

   public void testGetObject() {
      assertEquals(EnumFormatter.class.getName(), FormatterRegistry
            .getInstance().get(EnumClass.INSTANCE).getClass().getName());

      assertEquals(DefaultFormatter.class.getName(), FormatterRegistry
            .getInstance().get("").getClass().getName());

      assertEquals(DefaultFormatter.class.getName(), FormatterRegistry
            .getInstance().get(new Object()).getClass().getName());

      assertNull(FormatterRegistry.getInstance().get(null));
   }

   public void testRegister() {
      FormatterRegistry.getInstance().register(String.class, formatter);

      assertSame(formatter, FormatterRegistry.getInstance().get(String.class));
      assertSame(formatter, FormatterRegistry.getInstance().get(String.class,
            true));
      assertSame(formatter, FormatterRegistry.getInstance().get(String.class,
            false));
      assertSame(formatter, FormatterRegistry.getInstance().get(""));

      assertEquals(TEXT, FormatterRegistry.getInstance().format(""));

      FormatterRegistry.getInstance().register(String.class, null);
   }

   public void testFormat() {
      assertEquals("", FormatterRegistry.getInstance().format(null));

      Object value = new Object();
      assertEquals(value.toString(), FormatterRegistry.getInstance().
            format(value));

      assertEquals(new EnumFormatter().format(EnumClass.INSTANCE),
            FormatterRegistry.getInstance().format(EnumClass.INSTANCE));
   }
}