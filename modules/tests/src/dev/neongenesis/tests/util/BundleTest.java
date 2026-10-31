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
package dev.neongenesis.tests.util;

import dev.neongenesis.util.Bundle;
import java.util.Locale;
import java.util.MissingResourceException;
import dev.neongenesis.tests.TestCase;

public class BundleTest extends TestCase {
   public void testGetMessage() {
      final Locale defaultLocale = Locale.getDefault();
      Locale.setDefault(Locale.ENGLISH);
      try {
         Bundle.getMessage(BundleTest.class, "falt");
         fail("A MissingResourceException should be thrown if a invalid " +
               "resource name is informed");
      } catch (MissingResourceException mre) {
      }
      assertEquals("Test", Bundle.getMessage(BundleTest.class, "test"));
      assertEquals("Default", Bundle.getMessage(BundleTest.class, "default"));
      assertEquals("Message from genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", new Object[] {"Message", "genesis"}));
      assertEquals("Message from genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", "Message", "genesis"));
      assertEquals("Message from genesis", Bundle.getMessage(BundleTest.class,
            "message_0", "genesis"));


      Locale pt_BR = new Locale("pt", "BR");
      Locale.setDefault(pt_BR);
      assertEquals("Teste", Bundle.getMessage(BundleTest.class, "test"));
      assertEquals("Default", Bundle.getMessage(BundleTest.class, "default"));
      assertEquals("Mensagem do genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", new Object[] {"Mensagem", "genesis"}));
      assertEquals("Mensagem do genesis", Bundle.getMessage(BundleTest.class,
            "message_0_1", "Mensagem", "genesis"));
      assertEquals("Mensagem do genesis", Bundle.getMessage(BundleTest.class,
            "message_0", "genesis"));

      Locale.setDefault(defaultLocale);
   }

   public void testGetBundle() {
      try {
         Bundle.getBundle("Error", Locale.getDefault(), Thread.currentThread().
               getContextClassLoader());
         fail("A MissingResourceException should be thrown if a invalid " +
               "bundle name is informed");
      } catch (MissingResourceException mre) {
      }

      Bundle.getBundle(BundleTest.class.getPackage().getName() + ".Bundle",
            Locale.getDefault(), Thread.currentThread().getContextClassLoader());

      Bundle.getBundle(BundleTest.class);
   }
}
