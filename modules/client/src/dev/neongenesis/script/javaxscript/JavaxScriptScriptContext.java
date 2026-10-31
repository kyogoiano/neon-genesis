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
package dev.neongenesis.script.javaxscript;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Map;
import javax.script.ScriptEngine;

import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptException;
import dev.neongenesis.script.ScriptExpression;

public class JavaxScriptScriptContext extends ScriptContext {
   public static final String FORM_NS = "form"; // NOI18N
   public static final String GENESIS_FUNCTIONS_NS = "genesis"; // NOI18N

   private final ScriptEngine scriptEngine;
   private final Map<String, Object> unmodifiableMap;
   private final javax.script.ScriptContext realContext;

   protected JavaxScriptScriptContext(ScriptEngine engine, Object root) {
      this.scriptEngine = engine;
      this.realContext = engine.getContext();
      this.unmodifiableMap = Collections.unmodifiableMap(
            this.realContext.getBindings(javax.script.ScriptContext.ENGINE_SCOPE));
      declare(FORM_NS, root);
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }

   protected ScriptEngine getScriptEngine() {
      return scriptEngine;
   }

   protected Object doEval(ScriptExpression expr) {
      try {
         return ((JavaxScriptExpression) expr).evalCompiled(scriptEngine,
               realContext);
      } catch (InvocationTargetException ite) {
         throw new ScriptException(ite.getTargetException().getMessage(), ite.getTargetException());
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new JavaxScriptExpression(expression, scriptEngine);
   }

   public void registerFunctions(String prefix, Class functionClass) {
      try {
         scriptEngine.getContext().setAttribute(prefix,
               functionClass.getDeclaredConstructor().newInstance(),
               javax.script.ScriptContext.ENGINE_SCOPE);
      } catch (Exception e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   public void declare(String name, Object value) {
      scriptEngine.getContext().setAttribute(name, value,
            javax.script.ScriptContext.ENGINE_SCOPE);
   }

   public Map getContextMap() {
      return unmodifiableMap;
   }

   public Object lookup(String name) {
      return scriptEngine.getContext().getAttribute(name);
   }

   public void undeclare(String name) {
      scriptEngine.getContext().removeAttribute(name,
            javax.script.ScriptContext.ENGINE_SCOPE);
   }

   protected Class getFunctions() {
      return JavaxScriptFunctions.class;
   }
}
