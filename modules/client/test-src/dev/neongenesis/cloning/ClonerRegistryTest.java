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
package dev.neongenesis.cloning;

import java.util.Collections;

import dev.neongenesis.GenesisTestCase;

public class ClonerRegistryTest extends GenesisTestCase {
   public static final class SomeCloner implements Cloner {
      private boolean acceptNull;

      public Object clone(Object o) {
         if (o == null && !acceptNull) {
            throw new IllegalArgumentException();
         }

         return o;
      }

      public boolean isAcceptNull() {
         return acceptNull;
      }

      public void setAcceptNull(boolean acceptNull) {
         this.acceptNull = acceptNull;
      }

   }

   public ClonerRegistryTest() {
      super("Cloner Registry Unit Test");
   }

   protected void tearDown() throws Exception {
      ClonerRegistry.getInstance().register(String.class, null);
   }

   public void testGetDefaultEmptyResolverForClass() {
      assertEquals(DefaultCloner.class, ClonerRegistry.getInstance()
            .getDefaultClonerFor(Object.class).getClass());
      assertEquals(DefaultCloner.class, ClonerRegistry.getInstance()
            .getDefaultClonerFor(String.class).getClass());
   }

   public void testGetCloner() {
      assertTrue(((SomeCloner)ClonerRegistry.getInstance().getCloner(
            SomeCloner.class.getName(),
            Collections.singletonMap("acceptNull", "true"))).isAcceptNull());
   }

   public void testGetDefaultClonerForClassMap() {
      assertEquals(DefaultCloner.class, ClonerRegistry.getInstance()
            .getDefaultClonerFor(String.class, null).getClass());

      ClonerRegistry.getInstance().register(String.class, new SomeCloner());

      assertFalse(((SomeCloner)ClonerRegistry.getInstance()
            .getDefaultClonerFor(String.class)).isAcceptNull());

      assertTrue(((SomeCloner)ClonerRegistry.getInstance().getDefaultClonerFor(
            String.class, Collections.singletonMap("acceptNull", "true")))
            .isAcceptNull());
   }

}
