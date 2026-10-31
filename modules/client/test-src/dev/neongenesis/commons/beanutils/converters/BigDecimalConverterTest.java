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

import java.math.BigDecimal;

import dev.neongenesis.GenesisTestCase;

public class BigDecimalConverterTest extends GenesisTestCase {
   public void testBigDecimalConverter() {
      BigDecimal bigDecimal = new BigDecimal("5.2");

      final BigDecimalConverter converter = new BigDecimalConverter();

      // Using a BigDecimal instance
      assertSame(converter.convert(BigDecimal.class, bigDecimal), bigDecimal);

      // Using null and unassignable class
      assertNull(converter.convert(BigDecimal.class, null));
      assertNull(converter.convert(BigDecimal.class, new Object()));

      // Testing with an empty String
      assertNull(converter.convert(BigDecimal.class, ""));

      // Testing with "regular" values
      assertTrue(new BigDecimal("0.0").compareTo((BigDecimal) converter
            .convert(BigDecimal.class, "0,0")) == 0);
      assertEquals(new BigDecimal("2.23"), converter.convert(BigDecimal.class,
            "2,23"));
      assertNull(converter.convert(BigDecimal.class, "2,22A"));
      assertEquals(bigDecimal.toString(), "5.2");
   }
}
