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
package dev.neongenesis.script;

import java.util.Map;

import dev.neongenesis.equality.EqualityComparator;
import dev.neongenesis.equality.EqualityComparatorRegistry;
import dev.neongenesis.resolvers.EmptyResolver;
import dev.neongenesis.resolvers.EmptyResolverRegistry;
import dev.neongenesis.ui.controller.FormController;
import dev.neongenesis.ui.controller.FormState;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;

import dev.neongenesis.util.Bundle;
import org.apache.commons.beanutils.PropertyUtils;

public class ScriptUtils {
   private static final ThreadLocal current = new ThreadLocal();

   public static boolean supportsJavaxScript() {
      return true;
   }

   public static ScriptContext getCurrentContext() {
      return (ScriptContext) current.get();
   }

   public static void enter(ScriptContext ctx) {
      current.set(ctx);
   }

   public static void exit() {
      current.set(null);
   }

   public static Object getPropertyValue(String objKey, String propertyName) {
      final Object bean = getCurrentContext().lookup(objKey);
      if (bean == null) {
         throw new IllegalArgumentException(Bundle.getMessage(ScriptUtils.class,
               "BEAN_CALLED_X_NOT_FOUND", objKey)); // NOI18N
      }
      try {
         return PropertyUtils.getProperty(bean, propertyName);
      } catch (Exception e) {
         throw new IllegalArgumentException(Bundle.getMessage(ScriptUtils.class,
               "BEAN_NAMED_X_Y", objKey, e.getMessage())); // NOI18N
      }
   }

   public static ScriptValue getScriptValue(Object obj) {
      if (obj instanceof String) {
         String s = obj.toString();

         String[] values = s.split(":"); // NOI18N

         if (values.length == 1) {
            return new ScriptValue(null, obj);
         }

         if (values.length != 2) {
            throw new IllegalArgumentException(Bundle.getMessage(
                  ScriptUtils.class, "MALFORMED_EXPRESSION_X", obj)); // NOI18N
         }

         return new ScriptValue(values[1], getPropertyValue(values[0],
               values[1]));
      }

      return new ScriptValue(null, obj);
   }

   public static FormMetadata getFormMetadata(ScriptContext ctx) {
      return (FormMetadata) ctx.lookup(FormController.FORM_METADATA_KEY);
   }

   public static Map getChangedMap(ScriptContext ctx) {
      return ((FormState) ctx.lookup(FormController.CURRENT_STATE_KEY))
            .getChangedMap();
   }

   public static EmptyResolver getEmptyResolver(ScriptContext ctx,
         ScriptValue scriptValue) {
      if (scriptValue.getFieldName() == null) {
         return EmptyResolverRegistry.getInstance().getDefaultEmptyResolverFor(
               scriptValue.getValue() == null ? Object.class : scriptValue
                     .getValue().getClass());
      }

      final FormMetadata meta = getFormMetadata(ctx);
      final FieldMetadata fieldMetadata = meta.getFieldMetadata(scriptValue
            .getFieldName());

      if (fieldMetadata != null) {
         return fieldMetadata.getEmptyResolver();
      }

      return (EmptyResolver) EmptyResolverRegistry.getInstance().get(
            scriptValue.getValue());
   }

   public static EqualityComparator getEqualityComparator(ScriptContext ctx,
         ScriptValue scriptValue) {
      if (scriptValue.getFieldName() == null) {
         return EqualityComparatorRegistry.getInstance()
               .getDefaultEqualityComparatorFor(
                     scriptValue.getValue() == null ? Object.class
                           : scriptValue.getValue().getClass());
      }

      final FormMetadata meta = getFormMetadata(ctx);
      final FieldMetadata fieldMetadata = meta.getFieldMetadata(scriptValue
            .getFieldName());

      if (fieldMetadata != null) {
         return fieldMetadata.getEqualityComparator();
      }

      return EqualityComparatorRegistry.getInstance()
            .getDefaultEqualityComparatorFor(scriptValue.getValue().getClass());
   }
}