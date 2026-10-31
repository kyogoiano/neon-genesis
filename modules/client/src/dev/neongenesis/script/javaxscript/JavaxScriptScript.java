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

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import dev.neongenesis.script.Script;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptExpression;

/**
 * {@link Script} implementation backed by a JSR-223 {@link javax.script.ScriptEngine}.
 *
 * <p>When the requested language is {@code "javascript"} the engine is resolved
 * using the following priority:</p>
 * <ol>
 *   <li>{@code "javascript"} — accepted by Rhino's JSR-223 adapter
 *       ({@code org.mozilla:rhino-engine}).</li>
 *   <li>{@code "rhino"} — alternative name registered by the same adapter.</li>
 * </ol>
 *
 * <p>A fresh engine is obtained per {@link ScriptContext} via
 * {@link #newContext(Object)} so that each form binding has its own isolated
 * variable scope.</p>
 */
public class JavaxScriptScript implements Script {

   private final ScriptEngineManager manager;
   /** Engine kept only for {@link #compile(String)}; not shared across contexts. */
   private final ScriptEngine compilationEngine;
   private final String lang;

   protected JavaxScriptScript(String lang) {
      this.lang = lang;
      this.manager = new ScriptEngineManager();
      this.compilationEngine = resolveEngine(lang);
   }

   private ScriptEngine resolveEngine(String language) {
      ScriptEngine resolved = manager.getEngineByName(language);
      if (resolved == null && "javascript".equalsIgnoreCase(language)) { // NOI18N
         resolved = manager.getEngineByName("rhino"); // NOI18N
      }
      if (resolved == null) {
         throw new IllegalStateException(
               "No JSR-223 ScriptEngine found for language: " + language); // NOI18N
      }
      return resolved;
   }

   public ScriptContext newContext(Object root) {
      return new JavaxScriptScriptContext(resolveEngine(lang), root);
   }

   public ScriptExpression compile(String expression) {
      return new JavaxScriptExpression(expression, compilationEngine);
   }
}

