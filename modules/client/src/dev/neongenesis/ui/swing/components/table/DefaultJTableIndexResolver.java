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
package dev.neongenesis.ui.swing.components.table;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import javax.swing.JComponent;
import dev.neongenesis.registry.Registry;
import dev.neongenesis.util.Bundle;

public class DefaultJTableIndexResolver implements JTableIndexResolver {
   protected static class JTableIndexResolverInfo {
      private Method convertRowIndexToModel;
      private Method convertRowIndexToView;

      JTableIndexResolverInfo(final Class clazz) {
         try {
            convertRowIndexToModel = clazz.getMethod(
                  "convertRowIndexToModel", new Class[] {int.class}); // NOI18N
            convertRowIndexToView = clazz.getMethod(
                  "convertRowIndexToView", new Class[] {int.class}); // NOI18N
         } catch (SecurityException se) {
            IllegalStateException ise = new IllegalStateException(
                  Bundle.getMessage(DefaultJTableIndexResolver.class,
                  "COULD_NOT_LOAD_CONVERTROWINDEXTO_METHODS")); // NOI18N
            ise.initCause(se);
            throw ise;
         } catch (NoSuchMethodException nsme) {
            // Expected
         }
      }

      public boolean needsConversion() {
         return convertRowIndexToModel != null;
      }

      public int convertRowIndexToModel(final JComponent component,
            final int index) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
         return (Integer) convertRowIndexToModel.invoke(component,
                 new Object[]{index});
      }

      public int convertRowIndexToView(final JComponent component,
            final int index) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
         return (Integer) convertRowIndexToView.invoke(component,
                 new Object[]{index});
      }
   }

   private static class JTableIndexResolverInfoRegistry {
      private final Registry registry = new Registry();

      public void deregister() {
         registry.deregister();
      }

      public void deregister(Class clazz) {
         registry.deregister(clazz);
      }

      public JTableIndexResolverInfo register(Class clazz,
            JTableIndexResolverInfo info) {
         return (JTableIndexResolverInfo)registry.register(clazz, info);
      }

      public JTableIndexResolverInfo get(Class clazz) {
         return (JTableIndexResolverInfo)registry.get(clazz);
      }

      public JTableIndexResolverInfo get(Class clazz, boolean superClass) {
         return (JTableIndexResolverInfo)registry.get(clazz, superClass);
      }

      public JTableIndexResolverInfo get(Object o) {
         return (JTableIndexResolverInfo)registry.get(o);
      }
   }
   private final Map resolverInfoMap = new WeakHashMap();
   private final JTableIndexResolverInfoRegistry resolverInfoRegistry =
         new JTableIndexResolverInfoRegistry();

   protected JTableIndexResolverInfo initialize(final Class clazz) {
      JTableIndexResolverInfo info = resolverInfoMap.get(clazz) != null ? (JTableIndexResolverInfo)resolverInfoMap.get(clazz)
            : resolverInfoRegistry.get(clazz, true);
      if (info == null) {
         info = new JTableIndexResolverInfo(clazz);
         if (info.needsConversion()) {
            resolverInfoRegistry.register(clazz, info);
         } else {
            resolverInfoMap.put(clazz, info);
         }
      }

      return info;
   }

   public boolean needsConversion(JComponent component) {
      return initialize(component.getClass()).needsConversion();
   }

   public int convertRowIndexToModel(JComponent component, int index) throws IllegalAccessException,
         IllegalArgumentException, InvocationTargetException {
      return initialize(component.getClass()).convertRowIndexToModel(component,
            index);
   }

   public int convertRowIndexToView(JComponent component, int index) throws IllegalAccessException,
         IllegalArgumentException, InvocationTargetException {
      return initialize(component.getClass()).convertRowIndexToView(component,
            index);
   }
}
