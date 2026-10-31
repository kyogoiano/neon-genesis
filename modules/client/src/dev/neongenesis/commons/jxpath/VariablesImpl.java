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
package dev.neongenesis.commons.jxpath;

import java.util.HashMap;
import java.util.Map;

import dev.neongenesis.util.Bundle;
import org.apache.commons.jxpath.Variables;

public class VariablesImpl implements Variables {

   private final Map vars = new HashMap();

   public boolean isDeclaredVariable(String varName) {
      return vars.containsKey(varName);
   }

   public Object getVariable(String varName) {
      if (vars.containsKey(varName)) {
         return vars.get(varName);
      }
      throw new IllegalArgumentException(Bundle.getMessage(VariablesImpl.class,
            "NO_SUCH_VARIABLE_X", varName)); // NOI18N
   }

   public void declareVariable(String varName, Object value) {
      vars.put(varName, value);
   }

   public void undeclareVariable(String varName) {
      vars.remove(varName);
   }

   public String toString() {
      return vars.toString();
   }
   
   public Map getVariablesMap(){
      return vars;
   }
}