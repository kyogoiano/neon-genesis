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
package dev.neongenesis.ui.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import dev.neongenesis.annotation.AfterAction;
import dev.neongenesis.annotation.BeforeAction;

import dev.neongenesis.util.Bundle;

public class DefaultViewMetadataFactory implements ViewMetadataFactory {
   private boolean skipSystemClasses = true;

   public void setSkipSystemClasses(boolean skip) {
      this.skipSystemClasses = skip;
   }

   public ViewMetadata getViewMetadata(final Class viewClass) {
      ViewMetadata viewMetadata = ViewMetadataRegistry.getInstance().get(viewClass);

      if (viewMetadata == null) {
         viewMetadata = createViewMetadata(viewClass);
         processAnnotations(viewMetadata);
         ViewMetadataRegistry.getInstance().register(viewClass, viewMetadata);
      }

      return viewMetadata;
   }

   protected ViewMetadata createViewMetadata(Class viewClass) {
      return new ViewMetadata(viewClass);
   }

   private void processAnnotations(final ViewMetadata viewMetadata) {
      processMethodsAnnotations(viewMetadata);
   }

   private void processMethodsAnnotations(final ViewMetadata viewMetadata) {
      final Method[] methods = viewMetadata.getViewClass().getMethods();

      for (int i = 0; i < methods.length; i++) {
         if (skipSystemClasses
               && methods[i].getDeclaringClass().getClassLoader() == null) {
            continue;
         }

         processMethodAnnotation(viewMetadata, methods[i]);
      }
   }

   private void processMethodAnnotation(final ViewMetadata viewMetadata,
         Method method) {
      Annotation[] annotations = method.getAnnotations();

      for (int i = 0; i < annotations.length; i++) {
         if (BeforeAction.class.equals(annotations[i].annotationType())) {
            if (method.getParameterTypes().length != 0) {
               throw new IllegalArgumentException(Bundle.getMessage(
                     DefaultViewMetadataFactory.class,
                     "X_CANNOT_BE_USED_IN_METHOD_WITH_PARAMETERS_Y", "@BeforeAction", method.toString())); // NOI18N
            }

            BeforeAction annon = (BeforeAction) annotations[i];
            final String[] actionNames = annon.value();
            final String methodName = method.getName();

            if (actionNames == null || actionNames.length == 0) {
               viewMetadata.addBeforeAction(methodName, methodName);
               continue;
            }

            for (int j = 0; j < actionNames.length; j++) {
               viewMetadata.addBeforeAction(actionNames[j], methodName);
            }
         } else if (AfterAction.class.equals(annotations[i].annotationType())) {
            if (method.getParameterTypes().length != 0) {
               throw new IllegalArgumentException(Bundle.getMessage(
                     DefaultViewMetadataFactory.class,
                     "X_CANNOT_BE_USED_IN_METHOD_WITH_PARAMETERS_Y", "@AfterAction", method.toString())); // NOI18N
            }

            AfterAction annon = (AfterAction) annotations[i];
            final String[] actionNames = annon.value();
            final String methodName = method.getName();

            if (actionNames == null || actionNames.length == 0) {
               viewMetadata.addAfterAction(methodName, methodName);
               continue;
            }

            for (int j = 0; j < actionNames.length; j++) {
               viewMetadata.addAfterAction(actionNames[j], methodName);
            }
         }
      }
   }
}
