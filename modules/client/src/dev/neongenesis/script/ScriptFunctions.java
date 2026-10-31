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

import dev.neongenesis.equality.EqualityComparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScriptFunctions {
   private static Log log = LogFactory.getLog(ScriptFunctions.class);

   public static boolean hasChanged(ScriptContext ctx, ScriptValue scriptValue) {
      final boolean result = ScriptUtils.getChangedMap(ctx).containsKey(
            scriptValue.getFieldName());

      if (log.isDebugEnabled()) {
         log.debug("Property '" + scriptValue.getFieldName() // NOI18N
               + (result ? "' changed" : "' didn't change")); // NOI18N
      }

      return result;
   }

   public static boolean isEmpty(ScriptContext ctx, ScriptValue scriptValue)
         throws Exception {
      return ScriptUtils.getEmptyResolver(ctx, scriptValue).isEmpty(
            scriptValue.getValue());
   }

   public static boolean equals(ScriptContext ctx, ScriptValue scriptValue1,
         ScriptValue scriptValue2) throws Exception {

      final EqualityComparator comp1 = ScriptUtils.getEqualityComparator(ctx,
            scriptValue1);
      final EqualityComparator comp2 = ScriptUtils.getEqualityComparator(ctx,
            scriptValue2);

      final Object value1 = scriptValue1.getValue();
      final Object value2 = scriptValue2.getValue();

      if (comp1 == comp2) {
         // Special case: Rhino's JSR-223 engine coerces all JavaScript numeric
         // literals (e.g. 123.0) to Long.  When the form field is a floating-
         // point type (Double/Float) and the script arg arrived as Long, compare
         // by double value rather than using the typed comparator which would
         // call Double.equals(Long) → false.
         // This mirrors the behaviour of the old BSF/Rhino bridge where the
         // custom WrapFactory preserved Double for JS number literals.
         if (isFloatFieldVsScriptLong(value1, value2)
               || isFloatFieldVsScriptLong(value2, value1)) {
            boolean result = Double.compare(((Number) value1).doubleValue(),
                  ((Number) value2).doubleValue()) == 0;
            if (log.isDebugEnabled()) {
               log.debug("Float-vs-Long numeric equals for '" + value1 // NOI18N
                     + "' and '" + value2 + "': " + result); // NOI18N
            }
            return result;
         }

         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2 // NOI18N
                  + "' with same comparator " + comp1); // NOI18N
         }
         return comp1.equals(value1, value2);
      } else if (value1 == null) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2 // NOI18N
                  + "'"); // NOI18N
         }
         return value2 == null;
      } else if (value2 == null) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2 // NOI18N
                  + "'"); // NOI18N
         }
         return false;
      } else if (value1.getClass().equals(value2.getClass())
            || value1.getClass().isAssignableFrom(value2.getClass())) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2 // NOI18N
                  + "' with first comparator " + comp1); // NOI18N
         }
         return comp1.equals(value1, value2);
      } else if (value2.getClass().isAssignableFrom(value1.getClass())) {
         if (log.isDebugEnabled()) {
            log.debug("Evaluation equals for '" + value1 + "' and '" + value2 // NOI18N
                  + "' with second comparator " + comp2); // NOI18N
         }
         return comp2.equals(value2, value1);
      }

      if (log.isDebugEnabled()) {
         log.debug("'" + value1 + "' and '" + value2 // NOI18N
               + "' have different classes"); // NOI18N
      }
      return false;
   }

   /**
    * Returns {@code true} when {@code fieldValue} is a Java floating-point
    * number ({@code Double} or {@code Float}) and {@code scriptValue} is a
    * {@code Long} produced by Rhino's JSR-223 coercion of a JavaScript numeric
    * literal.
    *
    * <p>This lets {@code genesis.equals('form:fieldDouble', 123.0)} return
    * {@code true} even though Rhino passes the JS literal as {@code Long(123)}.
    * Integer-typed fields are intentionally excluded so that, for example,
    * {@code Short(120)} and {@code Integer(120)} remain non-equal.</p>
    */
   private static boolean isFloatFieldVsScriptLong(Object fieldValue,
         Object scriptValue) {
      return (fieldValue instanceof Double || fieldValue instanceof Float)
            && scriptValue instanceof Long;
   }
}
