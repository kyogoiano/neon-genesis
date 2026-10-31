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
import java.util.Map;

import dev.neongenesis.reflection.ClassesCache;

import org.apache.commons.beanutils.BeanUtils;


public class RegistryFactory extends Registry {

   private Object clone(final Object obj, final Map attributes) {
      try {
         final Object clone = BeanUtils.cloneBean(obj);
         BeanUtils.populate(clone, attributes);
         return clone;
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      } catch (InstantiationException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
         Throwable cause = e.getTargetException();
         Throwable failure = cause == null ? e : cause;
         failure.printStackTrace();
         throw new RuntimeException(failure);
      } catch (NoSuchMethodException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   public Object get(Class clazz) {
      return clazz.isPrimitive() ? super.get(Object.class) : super.get(clazz);
   }

   public Object getNewInstance(final String className, final Map attributesMap) {
      try {
         final Object instance = ClassesCache.getClass(className).newInstance();
         if (attributesMap != null && !attributesMap.isEmpty()) {
            BeanUtils.populate(instance, attributesMap);
         }
         return instance;
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      } catch (InstantiationException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      } catch (IllegalAccessException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      } catch (InvocationTargetException e) {
         e.printStackTrace();
         throw new RuntimeException(e);
      }
   }

   public Object getExistingInstance(final Class clazz, final Map attributes) {
      final Object existingInstance = get(clazz, true);
      if (attributes == null || attributes.isEmpty()) {
         return existingInstance;
      }
      return clone(existingInstance, attributes);
   }

}