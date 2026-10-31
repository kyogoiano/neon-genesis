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

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import dev.neongenesis.script.ScriptExpression;

public class JavaxScriptExpression extends ScriptExpression {
   private CompiledScript realCompiledScript;

   public JavaxScriptExpression(String expr, ScriptEngine engine) {
      super(expr);
      compile(engine);
   }

   protected void compile(ScriptEngine engine) {
      if (engine instanceof Compilable) {
         try {
            realCompiledScript = ((Compilable) engine).compile(getExpressionString());
         } catch (Error | Exception e) {
            // Some engines (e.g. BeanShell) advertise Compilable but throw
            // at runtime — treat as non-compilable and fall back to eval.
            realCompiledScript = null;
         }
      }
   }

   public Object evalCompiled(ScriptEngine engine, ScriptContext realCtx) throws Exception {
      if (realCompiledScript == null) {
         return engine.eval(getExpressionString(), realCtx);
      }

      return realCompiledScript.eval(realCtx);
   }
}

