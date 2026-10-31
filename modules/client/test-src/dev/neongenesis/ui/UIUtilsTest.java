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
package dev.neongenesis.ui;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ResourceBundle;
import dev.neongenesis.GenesisTestCase;

public class UIUtilsTest extends GenesisTestCase {
   public void testMessagesLoadingWithMultipleClassLoaders() 
         throws ClassNotFoundException, NoSuchMethodException, 
         IllegalAccessException, IllegalArgumentException, 
         InvocationTargetException {
      ClassLoader cl = createClassLoader();
      ResourceBundle bundle = loadBundle(cl);
      
      cl = createClassLoader();
      Thread.currentThread().setContextClassLoader(cl);

      try {
         loadBundle(cl);
         fail("Shouldn't find messages.properties");
      } catch (InvocationTargetException ite) {
         assertTrue(ite.getCause() instanceof RuntimeException);
      }
   }
   
   private ClassLoader createClassLoader() {
      return new URLClassLoader(new URL[] {
            UIUtils.class.getProtectionDomain().getCodeSource().getLocation()}) {
         public InputStream getResourceAsStream(String name) {
            return name.endsWith("messages.properties") ? null : 
                  super.getResourceAsStream(name);
         }

         protected synchronized Class loadClass(String name, boolean resolve) 
               throws ClassNotFoundException {
            if (name.equals("messages")) {
               throw new ClassNotFoundException("messages");
            }

            if (!name.equals(UIUtils.class.getName())) {
               return super.loadClass(name, resolve);
            }

            Class clazz = findClass(name);
            
            if (resolve) {
               resolveClass(clazz);
            }

            return clazz;
         }

      };
   }

   public ResourceBundle loadBundle(ClassLoader cl) 
         throws ClassNotFoundException, NoSuchMethodException, 
         IllegalAccessException, IllegalArgumentException, 
         InvocationTargetException {
      Class clazz = cl.loadClass(UIUtils.class.getName());
      Method getInstance = clazz.getMethod("getInstance", EMPTY_CLASS_ARRAY);
      Object singleton = getInstance.invoke(null, EMPTY_OBJECT_ARRAY);
      Method getBundle = clazz.getMethod("getBundle", EMPTY_CLASS_ARRAY);

      return (ResourceBundle) getBundle.invoke(singleton, EMPTY_OBJECT_ARRAY);
   }
}