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

import dev.neongenesis.GenesisTestCase;

public class BeanUtilsClonerTest extends GenesisTestCase {
   public static class SomeBean {
      private Object field;

      public Object getField() {
         return field;
      }

      public void setField(Object field) {
         this.field = field;
      }
   }

   public static class BadBean {
      private String field = "Bad";

      public String getField() {
         return field;
      }

      public void setField(String field) {
         throw new IllegalStateException(field);
      }
   }

   public BeanUtilsClonerTest() {
      super("BeanUtils Cloner Unit Test");
   }

   public void testClone() {
      BeanUtilsCloner cloner = new BeanUtilsCloner();

      assertNull(cloner.clone(null));

      SomeBean bean = new SomeBean();
      bean.setField(new Object());
      SomeBean clone = (SomeBean)cloner.clone(bean);

      assertNotSame(bean, clone);
      assertSame(bean.getField(), clone.getField());

      BadBean badBean = new BadBean();
      
      try {
         cloner.clone(badBean);
         fail("RuntimeException should have been thrown!");
      } catch (RuntimeException re) {
      }
   }
}
