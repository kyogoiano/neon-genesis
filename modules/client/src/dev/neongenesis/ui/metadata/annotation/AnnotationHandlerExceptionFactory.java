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

import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.MemberMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;
import dev.neongenesis.util.Bundle;

class AnnotationHandlerExceptionFactory {
   private AnnotationHandlerExceptionFactory() {
   }

   private static String getAnnotationName(final String annotationName) {
      return "@" + annotationName; // NOI18N
   }

   public static String getMemberName(final FormMetadata formMetadata,
         final MemberMetadata memberMetadata) {
      return Bundle.getMessage(AnnotationHandlerExceptionFactory.class,
            "AT_MEMBER_X_Y", formMetadata.  // NOI18N
            getFormClass().getName(), memberMetadata.getName());
   }

   public static String getMethodName(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata) {
      final String[] argsClassNames = methodMetadata.getMethodEntry().
            getArgsClassesNames();

      final StringBuffer classNames = new StringBuffer();
      for (int i = 0; i < argsClassNames.length; i++) {
         if (i > 0) {
            classNames.append(", "); // NOI18N
         }

         classNames.append(argsClassNames[i]);
      }

      return Bundle.getMessage(AnnotationHandlerExceptionFactory.class,
            "AT_X_Y_Z", new Object[] {formMetadata.getFormClass().getName(), // NOI18N
               methodMetadata.getName(), classNames});
   }

   public static void notFormAnnotation(final FormMetadata formMetadata, 
         final String annotationName) {
      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_CANNOT_BE_USED_AS_A_FORM_ANNOTATION_AT_Y", // NOI18N
            getAnnotationName(annotationName),
            formMetadata.getFormClass().getName()));
   }

   public static void notFieldAnnotation(final FormMetadata formMetadata, 
         final FieldMetadata fieldMetadata, String annotationName, 
         final boolean methodIsValid) {
      final String key = methodIsValid ? "X_CANNOT_BE_USED_TO_ANNOTATE_A_PROPERTY_FIX_SUGGESTION_Y" // NOI18N
            : "X_CANNOT_BE_USED_TO_ANNOTATE_A_PROPERTY_Y"; // NOI18N
      annotationName = getAnnotationName(annotationName);
      final String memberName = getMemberName(formMetadata, fieldMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class, key, annotationName,
            memberName));
   }

   public static void notMethodAnnotation(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_CANNOT_BE_USED_TO_ANNOTATE_A_METHOD_Y", annotationName, // NOI18N
            methodName));
   }


   public static void mustBeAction(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_MUST_BE_ACTION_Y", annotationName, methodName)); // NOI18N
   }

   public static void mustBePropertyOrDataProvider(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_MUST_BE_PROPERTY_OR_DATA_PROVIDER_Y", annotationName, methodName)); // NOI18N
   }

   public static void mustBePropertyOrAction(final FormMetadata formMetadata, 
         final MethodMetadata methodMetadata, String annotationName) {
      annotationName = getAnnotationName(annotationName);
      final String methodName = getMethodName(formMetadata, methodMetadata);

      throw new IllegalArgumentException(Bundle.getMessage(
            AnnotationHandlerExceptionFactory.class,
            "X_MUST_BE_PROPERTY_OR_ACTION_Y", annotationName, methodName)); // NOI18N
   }
}
