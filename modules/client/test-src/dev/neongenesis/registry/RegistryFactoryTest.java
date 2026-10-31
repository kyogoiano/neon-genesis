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
package dev.neongenesis.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import dev.neongenesis.GenesisTestCase;

public class RegistryFactoryTest extends GenesisTestCase {
   public static class SimpleBean {
      static Throwable t;

      private String property;

      public SimpleBean() throws Throwable {
         if (t != null) {
            throw t;
         }
      }

      public SimpleBean(String property) {
         setProperty(property);
      }

      public String getProperty() {
         return property;
      }

      public void setProperty(String property) {
         this.property = property;
      }

      // Just in case...
      public int hashCode() {
         return 0;
      }

      public boolean equals(Object o) {
         if (!(o instanceof SimpleBean)) {
            return false;
         }

         SimpleBean s = (SimpleBean)o;
         return property == null ? s.getProperty() == null : 
               getProperty().equals(s.getProperty());
      }
   }

   private RegistryFactory factory;

   public void setUp() {
      factory = new RegistryFactory();
      SimpleBean.t = null;
   }

   public void testGet() {
      factory.register(Object.class, new Object());
      factory.register(Integer.class, 1);

      // Test for regular class with registered instance
      assertEquals(Integer.class, factory.get(Integer.class).getClass());

      // Test for regular class with no registered instance
      assertNull(factory.get(Comparable.class));

      // Test for primitive
      assertEquals(Object.class, factory.get(Byte.TYPE).getClass());

      // Test for Object
      assertEquals(Object.class, factory.get(Object.class).getClass());
   }

   public void testGetNewInstance() throws Throwable {
      // Test with no properties
      assertEquals(new SimpleBean(), factory.getNewInstance(SimpleBean.class
            .getName(), null));
      assertEquals(new SimpleBean(), factory.getNewInstance(SimpleBean.class
            .getName(), new HashMap()));

      // Test with properties
      Map attributes = new HashMap();
      attributes.put("property", "value");

      assertEquals(new SimpleBean("value"), factory.getNewInstance(
            SimpleBean.class.getName(), attributes));

      // Test for non-existing class
      try {
         factory.getNewInstance("NonExistingClass", null);
         fail("Should have thrown a wrapped ClassNotFoundException");
      } catch (RuntimeException re) {
         assertEquals(ClassNotFoundException.class, re.getCause().getClass());
      }

      checkExceptionGetNewInstance(new IllegalAccessException());
      checkExceptionGetNewInstance(new InvocationTargetException(new Exception()));
      checkExceptionGetNewInstance(new InstantiationException());
   }

   private void checkExceptionGetNewInstance(Exception e) {
      SimpleBean.t = e;

      try {
         factory.getNewInstance(SimpleBean.class.getName(), null);
         fail("Should have thrown a wrapped " + e.getClass().getName());
      } catch (RuntimeException re) {
         assertEquals(e.getClass(), re.getCause().getClass());
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }

   public void testGetExistingInstance() throws Throwable {
      SimpleBean instance = new SimpleBean();
      factory.register(SimpleBean.class, instance);

      // Test with no properties
      assertSame(instance, factory.getExistingInstance(SimpleBean.class, null));
      assertSame(instance, factory.getExistingInstance(SimpleBean.class, 
            new HashMap()));

      // Test with properties
      Map attributes = new HashMap();
      attributes.put("property", "value");

      assertEquals(new SimpleBean("value"), factory.getExistingInstance(
            SimpleBean.class, attributes));

      checkExceptionGetExistingInstance(new NoSuchMethodException());
      checkExceptionGetExistingInstance(new IllegalAccessException());
      checkExceptionGetExistingInstance(new InvocationTargetException(
            new Exception()));
      checkExceptionGetExistingInstance(new InstantiationException());
   }

   private void checkExceptionGetExistingInstance(Exception e) {
      SimpleBean.t = e;

      Map attributes = new HashMap();
      attributes.put("property", "value");

      try {
         factory.getExistingInstance(SimpleBean.class, attributes);
         fail("Should have thrown a wrapped " + e.getClass().getName());
      } catch (RuntimeException re) {
         assertEquals(e.getClass(), re.getCause().getClass());
      } catch (Throwable t) {
         throw new RuntimeException(t);
      }
   }
}