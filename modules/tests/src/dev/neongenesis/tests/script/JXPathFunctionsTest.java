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

import dev.neongenesis.annotation.Form;
import dev.neongenesis.commons.jxpath.JXPathContextFactory;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptFactory;
import dev.neongenesis.script.jxpath.JXPathScriptContext;
import dev.neongenesis.script.jxpath.JXPathScriptFactory;

@Form
public class JXPathFunctionsTest extends ScriptFunctionsTest {
   private static String staticValue;

   static {
      System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
            JXPathContextFactory.class.getName());
      staticValue = null;
   }

   protected ScriptFactory newScriptFactory() {
      return new JXPathScriptFactory();
   }

   protected String toScriptMethod(String methodName, String expression) {
      return "g:" + methodName + '(' + expression + ')';
   }

   protected String toScriptBoolean(boolean b) {
      return String.valueOf(b) + "()";
   }

   protected String toScriptByte(byte b) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toByte("
            + String.valueOf(b) + ')';
   }

   protected String toScriptChar(char c) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toChar('"
            + String.valueOf(c) + "')";
   }

   protected String toScriptFloat(float f) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toFloat("
            + String.valueOf(f) + ')';
   }

   protected String toScriptInt(int i) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toInt("
            + String.valueOf(i) + ')';
   }

   protected String toScriptLong(long l) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toLong("
            + String.valueOf(l) + ')';
   }

   protected String toScriptShort(short s) {
      return JXPathScriptContext.PRIMITIVE_FUNCTIONS_NS + ":toShort("
            + String.valueOf(s) + ')';
   }

   protected String toScriptField(String fieldName) {
      return fieldName.replace('.', '/');
   }

   protected String toScriptNot(String expression) {
      return "not(" + expression + ")";
   }

   protected String toScriptDouble(double d) {
      return String.valueOf(d);
   }

   protected String toScriptString(String string) {
      return '\'' + string + '\'';
   }
   
   public void testStaticFunction() throws Exception {
      ScriptContext ctx = getContext(this);
      String baseExpression = getClass().getName() + ".assign";
      
      String testValue = "test";
      ctx.eval(baseExpression + "('" + testValue + "')");
      assertEquals(testValue, staticValue);
   }

   public static final boolean assign(String value) {
      staticValue = value;
      return true;
   }
}
