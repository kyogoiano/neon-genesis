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

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dev.neongenesis.annotation.DataProvider;
import dev.neongenesis.reflection.FieldEntry;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;
import dev.neongenesis.util.Bundle;
import dev.neongenesis.util.GenesisUtils;

import org.apache.commons.beanutils.PropertyUtils;

public class DataProviderAnnotationHandler implements AnnotationHandler {
   public void processFormAnnotation(final FormMetadata formMetadata,
         final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFormAnnotation(formMetadata, 
            "DataProvider"); // NOI18N
   }

   public void processFieldAnnotation(final FormMetadata formMetadata,
         final FieldMetadata fieldMetadata, final Annotation annotation) {
      AnnotationHandlerExceptionFactory.notFieldAnnotation(formMetadata, 
            fieldMetadata, "DataProvider", true); // NOI18N
   }

   public void processMethodAnnotation(final FormMetadata formMetadata,
         final MethodMetadata methodMetadata, final Annotation annotation) {

      final DataProvider annon = (DataProvider) annotation;
      final String widgetName = annon.widgetName();
      final String objectFieldName = annon.objectField();
      final String indexFieldName = annon.indexField();
      final boolean callOnInit = annon.callOnInit();
      final boolean resetSelection = annon.resetSelection();

      final DataProviderMetadata dataProviderMetadata = methodMetadata
            .getDataProviderMetadata();

      final String methodName =
            AnnotationHandlerExceptionFactory.getMethodName(formMetadata,
            methodMetadata);

      if (GenesisUtils.isBlank(widgetName)
            && GenesisUtils.isBlank(objectFieldName)
            && GenesisUtils.isBlank(indexFieldName)) {
         String errorMessage = Bundle.getMessage(
               DataProviderAnnotationHandler.class,
               "AT_LEAST_ONE_OF_OPTIONS_MUST_BE_SPECIFIED_FOR_DATAPROVIDER_X", // NOI18N
               methodName);
         throw new IllegalArgumentException(errorMessage);
      }

      dataProviderMetadata.setCallOnInit(callOnInit);
      dataProviderMetadata.setResetSelection(resetSelection);

      PropertyDescriptor[] descriptors = PropertyUtils
            .getPropertyDescriptors(formMetadata.getFormClass());

      Map descriptorsPerPropertyName = new HashMap();

      for (int i = 0; i < descriptors.length; i++) {
         descriptorsPerPropertyName.put(descriptors[i].getName(),
               descriptors[i]);
      }

      if (!GenesisUtils.isBlank(objectFieldName)) {
         PropertyDescriptor descriptor = (PropertyDescriptor) descriptorsPerPropertyName
               .get(objectFieldName);
         if (descriptor == null || descriptor.getReadMethod() == null ||
               descriptor.getWriteMethod() == null) {

            StringBuffer propertyName = new StringBuffer().append(Character
                  .toUpperCase(objectFieldName.charAt(0)));

            if (objectFieldName.length() > 1) {
               propertyName.append(objectFieldName.substring(1));
            }

            String key =
                  descriptor == null ? "DATAPROVIDER_OBJECTFIELD_REFERS_TO_X_BUT_PUBLIC_PROPERTY_DOES_NOT_EXIST" : // NOI18N
                  "DATAPROVIDER_OBJECTFIELD_REFERS_TO_X_BUT_IT_IS_NOT_A_READ_WRITE_PROPERTY"; // NOI18N
            String errorMessage = Bundle.getMessage(
                  DataProviderAnnotationHandler.class, key, new Object[] {
                     objectFieldName, propertyName, methodName});

            throw new IllegalArgumentException(errorMessage);
         }

         final Class fieldType = descriptor.getPropertyType();

         if (fieldType.isPrimitive()
               || (fieldType.isArray() && fieldType.getComponentType()
                     .isPrimitive())) {
            String errorMessage = Bundle.getMessage(
                  DataProviderAnnotationHandler.class,
                  "DATAPROVIDER_OBJECTFIELD_REFERS_TO_X_BUT_IT_IS_NOT_A_PRIMITIVE_OR_ARRAY_OF_PRIMITIVES", // NOI18N
                  objectFieldName, methodName);

            throw new IllegalArgumentException(errorMessage);
         }

         dataProviderMetadata.setObjectField(new FieldEntry(descriptor
               .getName(), descriptor.getPropertyType()));
      }

      if (!GenesisUtils.isBlank(indexFieldName)) {
         PropertyDescriptor descriptor = (PropertyDescriptor) descriptorsPerPropertyName
               .get(indexFieldName);

         if (descriptor == null || descriptor.getReadMethod() == null ||
               descriptor.getWriteMethod() == null) {
            final StringBuffer propertyName = new StringBuffer().append(Character
                  .toUpperCase(indexFieldName.charAt(0)));

            if (indexFieldName.length() > 1) {
               propertyName.append(indexFieldName.substring(1));
            }

            String key =
                  descriptor == null ? "DATAPROVIDER_INDEXFIELD_REFERS_TO_X_BUT_SUCH_PUBLIC_PROPERTY_DOES_NOT_EXIST" : // NOI18N
                  "DATAPROVIDER_INDEXFIELD_REFERS_TO_X_BUT_IT_IS_NOT_A_READ_WRITE_PROPERTY"; // NOI18N
            String errorMessage = Bundle.getMessage(
                  DataProviderAnnotationHandler.class, key, new Object[] {
                     indexFieldName, propertyName, methodName});
            throw new IllegalArgumentException(errorMessage);
         }

         final Class fieldType = descriptor.getPropertyType().isArray() ? descriptor
               .getPropertyType().getComponentType()
               : descriptor.getPropertyType();

         if (!Collection.class.isAssignableFrom(descriptor.getPropertyType())
               && !Integer.TYPE.isAssignableFrom(fieldType)
               && !Integer.class.isAssignableFrom(fieldType)) {
            String errorMessage = Bundle.getMessage(
                  DataProviderAnnotationHandler.class,
                  "DATAPROVIDER_INDEXFIELD_REFERS_TO_X_BUT_IT_IS_NOT_AN_INTEGER_INT_ARRAY_COLLECTION", // NOI18N
                  indexFieldName, methodName);
            throw new IllegalArgumentException(errorMessage);
         }

         dataProviderMetadata.setIndexField(new FieldEntry(
               descriptor.getName(), descriptor.getPropertyType()));
      }

      dataProviderMetadata
            .setWidgetName(!GenesisUtils.isBlank(widgetName) ? widgetName
                  : !GenesisUtils.isBlank(objectFieldName) ? objectFieldName
                        : indexFieldName);
   }
}