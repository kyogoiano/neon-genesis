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
package dev.neongenesis.mockobjects;

import java.util.Map;
import dev.neongenesis.script.Script;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptExpression;
import dev.neongenesis.script.ScriptFactory;

public class MockScriptFactory implements ScriptFactory {
   public Script newScript() {
      return new Script() {
         public ScriptContext newContext(Object root) {
            return new MockScriptContext();
         }

         public ScriptExpression compile(String expression) {
            return new MockScriptExpression();
         }
      };
   }
}

class MockScriptContext extends ScriptContext {
   public Object lookup(String name) {
      return null;
   }

   public void declare(String name, Object value) {
   }

   public void undeclare(String name) {
   }

   public void registerFunctions(String prefix, Class functionClass) {
   }

   public Map getContextMap() {
      return null;
   }

   protected Object doEval(ScriptExpression expr) {
      return null;
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new MockScriptExpression();
   }
}

class MockScriptExpression extends ScriptExpression {
   public MockScriptExpression() {
      super(null);
   }
}