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

import java.util.Collections;
import java.util.Map;

import dev.neongenesis.commons.jxpath.VariablesImpl;
import dev.neongenesis.script.PrimitiveFunctions;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptExpression;

import org.apache.commons.jxpath.ClassFunctions;
import org.apache.commons.jxpath.FunctionLibrary;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Variables;

public class JXPathScriptContext extends ScriptContext {
   public static final String GENESIS_FUNCTIONS_NS = "g"; // NOI18N
   public static final String PRIMITIVE_FUNCTIONS_NS = "t"; // NOI18N

   private final JXPathContext ctx;
   private final Map unmodifiableMap;
   private final FunctionLibrary functionLib;

   protected JXPathScriptContext(Object root) {
      ctx = JXPathContext.newContext(root);
      functionLib = createFunctionLibrary();
      ctx.setFunctions(functionLib);
      ctx.setVariables(getVariables());
      unmodifiableMap = Collections.unmodifiableMap(((VariablesImpl) 
            ctx.getVariables()).getVariablesMap());
      registerFunctions(PRIMITIVE_FUNCTIONS_NS, PrimitiveFunctions.class);
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }

   protected FunctionLibrary createFunctionLibrary() {
      FunctionLibrary functionLib = new FunctionLibrary();
      functionLib.addFunctions(ctx.getFunctions());

      return functionLib;
   }

   protected Object doEval(ScriptExpression expr) {
      return ctx.getValue(expr.getExpressionString());
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new JXPathExpression(expression);
   }

   public void registerFunctions(String prefix, Class functionClass) {
      functionLib.addFunctions(new ClassFunctions(functionClass, prefix));
   }

   public void declare(String name, Object value) {
      ctx.getVariables().declareVariable(name, value);
   }

   public Object lookup(String name) {
      return ctx.getVariables().getVariable(name);
   }

   public void undeclare(String name) {
      ctx.getVariables().undeclareVariable(name);
   }

   public Map getContextMap() {
      return unmodifiableMap;
   }

   protected Variables getVariables() {
      return new VariablesImpl();
   }

   protected Class getFunctions() {
      return JXPathFunctionsAdapter.class;
   }
}