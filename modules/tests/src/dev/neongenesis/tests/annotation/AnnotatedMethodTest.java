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
package dev.neongenesis.tests.annotation;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.tests.TestCase;

import java.lang.annotation.Annotation;

public class AnnotatedMethodTest extends TestCase {
   public void testNotOverridedMethod() throws Exception {
      ParentClass parent = new ParentClass();

      Annotation an = parent
               .getClass().getMethod("method", new Class[0]).getAnnotation(Remotable.class);


       assertNotNull(an);
      assertEquals(an.annotationType(), Remotable.class);

      parent = new ChildClass();

      an = parent.getClass()
            .getMethod("method", new Class[0]).getAnnotation(Remotable.class);

      assertNotNull(an);
      assertEquals(an.annotationType(), Remotable.class);

      final ChildClass child = new ChildClass();

      an = child.getClass()
            .getMethod("method", new Class[0]).getAnnotation(Remotable.class);

      assertNotNull(an);
      assertEquals(an.annotationType(), Remotable.class);
   }

   public void testOverridedMethod() throws Exception {
      ParentClass parent = new ParentClass();

      Annotation an = parent
            .getClass().getMethod("override", new Class[0]).getAnnotation(Remotable.class);

      assertNotNull(an);
      assertEquals(an.annotationType(), Remotable.class);

      parent = new ChildClass();

      an = parent.getClass()
            .getMethod("override", new Class[0]).getAnnotation(Remotable.class);

      assertNull(an);

      final ChildClass child = new ChildClass();

      an = child.getClass()
            .getMethod("override", new Class[0]).getAnnotation(Remotable.class);

      assertNull(an);
   }

   public static class ParentClass {
      @Remotable
      public void override() {
      }

      @Remotable
      public void method() {
      }
   }

   public static class ChildClass extends ParentClass {
      public void override() {
      }
   }
}