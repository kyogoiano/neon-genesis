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
package dev.neongenesis.ui.metadata.annotation;

import dev.neongenesis.annotation.VisibleWhen;
import dev.neongenesis.script.Script;
import dev.neongenesis.script.ScriptRegistry;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.MemberMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;

import dev.neongenesis.util.Bundle;

import java.lang.annotation.Annotation;

public class VisibleWhenAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata, 
            "VisibleWhen"); // NOI18N
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      processMemberAnnotation(formMetadata, fieldMetadata, annotation);
   }

   public void processMethodAnnotation(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Annotation annotation) {
      if (methodMetadata.getActionMetadata() == null) {
         AnnotationHandlerExceptionFactory.mustBePropertyOrAction(formMetadata, 
               methodMetadata, "VisibleWhen"); // NOI18N
      }

      processMemberAnnotation(formMetadata, methodMetadata
            .getActionMetadata(), annotation);
   }

   private void processMemberAnnotation(final FormMetadata formMetadata,
         final MemberMetadata memberMetadata, final Annotation annotation) {
      VisibleWhen annon = (VisibleWhen) annotation;
      String[] values = annon.value();
      final String memberName =
            AnnotationHandlerExceptionFactory.getMemberName(formMetadata,
            memberMetadata);
      if (values == null || values.length == 0) {
         throw new IllegalArgumentException(Bundle.getMessage(
               getClass(),
               "X_MUST_DEFINE_AT_LEAST_ONE_SCRIPT_CONDITION_Y", // NOI18N
               "VisibleWhen", memberName));
      }

      final Script script = formMetadata.getScript();

      if (values.length == 1) {
         memberMetadata.setVisibleCondition(script.compile(values[0]));
      } else if (values.length % 2 != 0) {
         throw new IllegalArgumentException(Bundle.getMessage(
               getClass(),
               "VISIBLEWHEN_MUST_DEFINE_AT_LEAST_ONE_SCRIPT_CONDITION_OR_PAIRS_OF_SCRIPT_CONDITIONS_X", // NOI18N
               memberName));
      } else {
         for (int i = 0; i < values.length; i += 2) {
            if (!ScriptRegistry.getInstance().isCurrentScriptFactoryNameFor(
                  values[i])) {
               continue;
            }

            memberMetadata.setVisibleCondition(script.compile(values[i + 1]));
            break;
         }
      }
   }
}
