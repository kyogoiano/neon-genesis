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

import java.lang.annotation.Annotation;
import java.util.Map;

import dev.neongenesis.resolvers.EmptyResolver;
import dev.neongenesis.resolvers.EmptyResolverRegistry;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;
import dev.neongenesis.util.GenesisUtils;


public class EmptyResolverAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata, 
            "EmptyResolver"); // NOI18N
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      fieldMetadata.setEmptyResolver(getEmptyResolver(fieldMetadata,
            (dev.neongenesis.annotation.EmptyResolver) annotation));
   }

   public void processMethodAnnotation(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notMethodAnnotation(formMetadata, 
            methodMetadata, "EmptyResolver"); // NOI18N
   }

   private EmptyResolver getEmptyResolver(final FieldMetadata fieldMetadata,
         final dev.neongenesis.annotation.EmptyResolver annon) {

      Map map = GenesisUtils.getAttributesMap(annon.properties());
      Class klazz = annon.value();
      if (klazz == null || Object.class.equals(klazz)) {
         return EmptyResolverRegistry.getInstance().getDefaultEmptyResolverFor(
               fieldMetadata.getFieldClass(), map);
      }
      return EmptyResolverRegistry.getInstance().getEmptyResolver(
            annon.value().getName(), map);
   }
}
