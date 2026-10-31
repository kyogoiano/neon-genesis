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

public abstract class ScriptContext {
   public abstract Object lookup(String name);
   public abstract void declare(String name, Object value);
   public abstract void undeclare(String name);
   public abstract void registerFunctions(String prefix, Class functionClass);
   public abstract Map getContextMap();

   protected abstract Object doEval(ScriptExpression expr);
   protected abstract ScriptExpression newScriptExpression(String expression);

   public Object eval(ScriptExpression expr) {
      ScriptUtils.enter(this);

      try {
         return doEval(expr);
      } finally {
         ScriptUtils.exit();
      }
   }

   public Object eval(String expression) {
      return eval(newScriptExpression(expression));
   }
}
