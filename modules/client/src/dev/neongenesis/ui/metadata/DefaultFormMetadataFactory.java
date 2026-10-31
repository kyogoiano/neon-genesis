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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import dev.neongenesis.annotation.Action;
import dev.neongenesis.annotation.DataProvider;
import dev.neongenesis.annotation.NotBound;
import dev.neongenesis.script.ScriptRegistry;
import dev.neongenesis.ui.metadata.annotation.AnnotationHandler;
import dev.neongenesis.ui.metadata.annotation.AnnotationHandlerRegistry;

import dev.neongenesis.util.Bundle;
import org.apache.commons.beanutils.PropertyUtils;

public class DefaultFormMetadataFactory implements FormMetadataFactory {
   public FormMetadata getFormMetadata(final Class formClass) {
      FormMetadata formMetadata = FormMetadataRegistry.getInstance().get(formClass);

      if (formMetadata == null) {
         formMetadata = new FormMetadata(formClass, ScriptRegistry.getInstance().getScript());
         processAnnotations(formMetadata);
         FormMetadataRegistry.getInstance().register(formClass, formMetadata);
      }

      return formMetadata;
   }

   private void processAnnotations(final FormMetadata formMetadata) {
      processFormAnnotations(formMetadata);
      processFieldsAnnotations(formMetadata);
      processMethodsAnnotations(formMetadata);
   }

   private void processFormAnnotations(final FormMetadata formMetadata) {
      final Annotation[] annotations = formMetadata
            .getFormClass().getAnnotations();
      AnnotationHandler handler;
      for (int i = 0; i < annotations.length; i++) {
         handler = AnnotationHandlerRegistry.getInstance().get(annotations[i].annotationType());

         if (handler == null) {
            continue;
         }

         handler.processFormAnnotation(formMetadata,
               annotations[i]);
      }
   }

   private void processFieldsAnnotations(final FormMetadata formMetadata) {
      final Class clazz = formMetadata.getFormClass();
      final PropertyDescriptor[] propertyDescriptors = PropertyUtils
            .getPropertyDescriptors(clazz);
      PropertyDescriptor propDesc;
      FieldMetadata fieldMetadata;
      for (int i = 0; i < propertyDescriptors.length; i++) {
         propDesc = propertyDescriptors[i];

         // Ignoring java.lang.Object.getClass()
         if (propDesc.getName().equals("class")) { // NOI18N
            continue;
         }

         final Method readMethod = getReadMethod(clazz, propDesc);

         if (readMethod == null ||
               readMethod.getAnnotation(NotBound.class) != null) {
            continue;
         }

         fieldMetadata = new FieldMetadata(propDesc.getName(), propDesc
               .getPropertyType(), propDesc.getWriteMethod() != null);
         formMetadata.addFieldMetadata(propDesc.getName(), fieldMetadata);
         processFieldAnnotations(formMetadata, fieldMetadata, readMethod);
      }
   }

   private void processFieldAnnotations(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Method fieldGetterMethod) {
      final Annotation[] annotations = fieldGetterMethod.getAnnotations();
      AnnotationHandler handler;
      for (int i = 0; i < annotations.length; i++) {
         handler = AnnotationHandlerRegistry.getInstance().get(annotations[i].annotationType());

         if (handler == null) {
            continue;
         }

         handler.processFieldAnnotation(formMetadata,
               fieldMetadata, annotations[i]);
      }
   }

   private void processMethodsAnnotations(final FormMetadata formMetadata) {
      final Method[] methods = formMetadata.getFormClass().getMethods();
      boolean isAction;
      boolean isProvider;
      MethodMetadata methodMetadata;

      for (int i = 0; i < methods.length; i++) {
         isAction = methods[i].getAnnotation(Action.class) != null;
         isProvider = methods[i].getAnnotation(DataProvider.class) != null;

         if (!isAction && !isProvider) {
            continue;
         }

         if (formMetadata.getFieldMetadata(methods[i].getName()) != null) {
            throw new IllegalArgumentException(Bundle.getMessage(
                  DefaultFormMetadataFactory.class, "AN_ACTION_OR_DATAPROVIDER_CANNOT_HAVE_THE_SAME_NAME_OF_A_PROPERTY", // NOI18N
                  methods[i].getName()));
         }

         methodMetadata = new MethodMetadata(methods[i], isAction, isProvider);
         formMetadata.addMethodMetadata(methods[i], methodMetadata);
         processMethodAnnotations(formMetadata, methodMetadata, methods[i]);
      }
   }

   private void processMethodAnnotations(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Method actionMethod) {
      final Annotation[] annotations = actionMethod.getAnnotations();
      AnnotationHandler handler;
      for (int i = 0; i < annotations.length; i++) {
         handler = AnnotationHandlerRegistry.getInstance().get(annotations[i].annotationType());

         if (handler == null) {
            continue;
         }

         handler.processMethodAnnotation(formMetadata,
               methodMetadata, annotations[i]);
      }
   }

   // Work around for a bug in JDK 1.4.2
   private Method getReadMethod(Class clazz, PropertyDescriptor pd) {
      Method method = pd.getReadMethod();

      if (method == null || clazz.equals(method.getDeclaringClass())) {
         return method;
      }

      try {
         method = clazz.getMethod(method.getName(), 
               method.getParameterTypes());
         return (method == null) ? pd.getReadMethod() : method;
      } catch (NoSuchMethodException nsme) {
         return method;
      }
   }
}
