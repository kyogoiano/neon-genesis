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
package dev.neongenesis.script.jxpath;

import dev.neongenesis.commons.jxpath.GenesisNodeSet;
import dev.neongenesis.script.ScriptFunctions;
import dev.neongenesis.script.ScriptUtils;
import dev.neongenesis.script.ScriptValue;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.jxpath.ExpressionContext;

public class JXPathFunctionsAdapter {
   private static Object getValue(final ExpressionContext ctx,
         final String propertyName) throws Exception {
      int indexOfDot = -1;

      while ((indexOfDot = propertyName.indexOf('.', indexOfDot + 1)) != -1) {
         if (PropertyUtils.getProperty(ctx.getJXPathContext().getContextBean(),
            propertyName.substring(0, indexOfDot)) == null) {
            return null;
         }
      }

      return PropertyUtils.getProperty(ctx.getJXPathContext().getContextBean(),
            propertyName);
   }

   private static String getSimplePropertyName(final GenesisNodeSet nodeSet) {
      return nodeSet.getPath().replace('/', '.');
   }

   private static ScriptValue getScriptValue(final ExpressionContext ctx,
         final Object obj) throws Exception {
      if (obj instanceof GenesisNodeSet) {
         return getScriptValue(ctx, (GenesisNodeSet)obj);
      }

      return new ScriptValue(null, obj);
   }

   private static ScriptValue getScriptValue(final ExpressionContext ctx,
         final GenesisNodeSet nodeSet) throws Exception {
      return new ScriptValue(getSimplePropertyName(nodeSet), getValue(ctx,
            getSimplePropertyName(nodeSet)));
   }

   public static boolean hasChanged(final ExpressionContext ctx,
         final GenesisNodeSet nodeSet) throws Exception {
      return ScriptFunctions.hasChanged(ScriptUtils.getCurrentContext(),
            getScriptValue(ctx, nodeSet));
   }

   public static boolean hasNotChanged(final ExpressionContext context,
         final GenesisNodeSet nodeSet) throws Exception {
      return !hasChanged(context, nodeSet);
   }

   public static boolean isEmpty(final ExpressionContext ctx, final Object obj)
         throws Exception {
      return ScriptFunctions.isEmpty(ScriptUtils.getCurrentContext(),
            getScriptValue(ctx, obj));
   }

   public static boolean isNotEmpty(final ExpressionContext ctx,
         final Object obj) throws Exception {
      return !isEmpty(ctx, obj);
   }

   public static boolean equals(final ExpressionContext ctx, final Object arg1,
         final Object arg2) throws Exception {
      return ScriptFunctions.equals(ScriptUtils.getCurrentContext(),
            getScriptValue(ctx, arg1), getScriptValue(ctx, arg2));
   }

   public static boolean notEquals(final ExpressionContext ctx,
         final Object arg1, final Object arg2) throws Exception {
      return !equals(ctx, arg1, arg2);
   }
}