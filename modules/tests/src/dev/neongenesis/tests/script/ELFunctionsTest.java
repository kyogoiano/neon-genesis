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
import dev.neongenesis.script.el.ELScriptFactory;

public class ELFunctionsTest extends ScriptFunctionsTest {

   protected ScriptFactory newScriptFactory() {
      return new ELScriptFactory();
   }

   protected String toScriptMethod(String methodName, String expression) {
      return "${g:" + methodName + '(' + expression + ")}";
   }

   protected String toScriptBoolean(boolean b) {
      return String.valueOf(b);
   }
   
   
   protected String toScriptByte(byte b) {
      return "toByte(" + String.valueOf(b) + ')';
   }

   protected String toScriptChar(char c) {
      return "toChar('" + String.valueOf(c) + "')";
   }

   protected String toScriptFloat(float f) {
      return "toFloat(" + String.valueOf(f) + ')';
   }

   protected String toScriptInt(int i) {
      return "toInt(" + String.valueOf(i) + ')';
   }

   protected String toScriptLong(long l) {
      return String.valueOf(l);
   }

   protected String toScriptShort(short s) {
      return "toShort(" + String.valueOf(s) + ')';
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