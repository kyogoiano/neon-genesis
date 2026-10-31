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
package dev.neongenesis.text;

import dev.neongenesis.script.Script;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptExpression;
import dev.neongenesis.ui.controller.FormController;
import dev.neongenesis.ui.controller.FormControllerRegistry;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.FormMetadataRegistry;
import dev.neongenesis.util.Bundle;

public class ScriptFormatter implements Formatter {
   public static final String ALIAS = "o"; // NOI18N

   private final Object form;
   private final FormController controller;
   private final Script script;
   private final ScriptExpression expression;
   private final String alias;

   public ScriptFormatter(Object form, String expression) {
      this(form, expression, ALIAS);
   }

   public ScriptFormatter(Object form, String expression, String alias) {
      final Class formClass = form.getClass();
      final FormMetadata formMetadata = FormMetadataRegistry.getInstance().get(
            formClass);

      if (formMetadata == null) {
         throw new IllegalArgumentException(Bundle.getMessage(ScriptFormatter.class,
               "X_HAS_NO_METADATA_REGISTERED", formClass)); // NOI18N
      }

      final FormController controller = FormControllerRegistry.getInstance().
            get(form);

      if (controller == null) {
         throw new IllegalArgumentException(Bundle.getMessage(ScriptFormatter.class,
               "X_HAS_NO_CONTROLLER_REGISTERED", form)); // NOI18N
      }

      this.form = form;
      this.controller = controller;
      this.script = formMetadata.getScript();
      this.expression = script.compile(expression);
      this.alias = alias;
   }

   public String format(Object o) {
      if (!controller.isSetup()) {
         throw new IllegalStateException(Bundle.getMessage(ScriptFormatter.class,
               "FORM_CONTROLLER_HAS_NOT_BEEN_SETUP")); // NOI18N
      }

      final ScriptContext scriptContext = controller.getScriptContext();

      if (scriptContext.getContextMap().containsKey(alias)) {
         throw new IllegalStateException(Bundle.getMessage(ScriptFormatter.class,
               "X_IS_ALREADY_PRESENT_IN_THE_SCRIPT_CONTEXT", alias)); // NOI18N
      }

      scriptContext.declare(alias, o);

      try {
         return FormatterRegistry.getInstance().format(expression.eval(
               scriptContext));
      } finally {
         scriptContext.undeclare(alias);
      }
   }
}
