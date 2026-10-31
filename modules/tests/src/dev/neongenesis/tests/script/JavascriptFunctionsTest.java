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
package dev.neongenesis.tests.script;

import dev.neongenesis.script.ScriptFactory;
import dev.neongenesis.script.javaxscript.JavaxScriptScriptFactory;

public class JavascriptFunctionsTest extends ScriptFunctionsTest {
   protected ScriptFactory newScriptFactory() {
      JavaxScriptScriptFactory factory = new JavaxScriptScriptFactory();
      factory.setLang("javascript");
      return factory;
   }

   protected String toScriptMethod(String methodName, String expression) {
      return "genesis." + methodName + '(' + expression + ')';
   }

   protected String toScriptBoolean(boolean b) {
      return String.valueOf(b);
   }

   protected String toScriptByte(byte b) {
      return "new java.lang.Byte(" + String.valueOf(b) + ')';
   }

   protected String toScriptChar(char c) {
      return "new java.lang.Character('" + c + "')";
   }

   protected String toScriptFloat(float f) {
      return "new java.lang.Float(" + String.valueOf(f) + ')';
   }

   protected String toScriptInt(int i) {
      return "new java.lang.Integer(" + String.valueOf(i) + ')';
   }

   protected String toScriptLong(long l) {
      return "new java.lang.Long(" + String.valueOf(l) + ')';
   }

   protected String toScriptShort(short s) {
      return "new java.lang.Short(" + String.valueOf(s) + ')';
   }

   protected String toScriptField(String fieldName) {
      return "'form:" + fieldName + "'";
   }

   protected String toScriptNot(String expression) {
      return "!(" + expression + ")";
   }

   protected String toScriptDouble(double d) {
      return String.valueOf(d);
   }

   protected String toScriptString(String string) {
      return '"' + string + '"';
   }

}
