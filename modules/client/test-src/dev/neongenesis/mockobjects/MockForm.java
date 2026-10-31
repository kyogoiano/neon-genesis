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
package dev.neongenesis.mockobjects;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import dev.neongenesis.reflection.FieldEntry;
import dev.neongenesis.script.jxpath.JXPathScript;
import dev.neongenesis.ui.controller.FormController;
import dev.neongenesis.ui.controller.FormControllerFactory;
import dev.neongenesis.ui.controller.MockFormController;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.FormMetadataFactory;
import dev.neongenesis.ui.metadata.MethodMetadata;

public class MockForm implements FormMetadataFactory, FormControllerFactory {
   private FormMetadata metadata = new FormMetadata(getClass(),
         new JXPathScript());

   private MockFormController controller = new MockFormController();

   private Map context;
   
   // Dummy properties
   private Object objectField;
   private String stringField;
   private int intField;

   public MockForm() {
      Method someAction = getMethod("someAction");
      Method someDataProvider = getMethod("someDataProvider");
      metadata.addFieldMetadata("stringField", new FieldMetadata("stringField",
            String.class, true));
      metadata.addFieldMetadata("objectField", new FieldMetadata("objectField",
            Object.class, true));
      metadata.addFieldMetadata("intField", new FieldMetadata("intField",
            Integer.TYPE, true));
      metadata.addMethodMetadata(someAction, new MethodMetadata(someAction,
            true, false));
      
      MethodMetadata methodMeta = new MethodMetadata(
            someDataProvider, true, true);
      metadata.addMethodMetadata(someDataProvider, methodMeta);
      methodMeta.getDataProviderMetadata().setWidgetName("dataProviderField");
      methodMeta.getDataProviderMetadata().setResetSelection(true);
      methodMeta.getDataProviderMetadata().setObjectField(new FieldEntry("objectField", Object.class));
   }

   public FormMetadata getFormMetadata(Class formClass) {
      return metadata;
   }

   public FormMetadata getFormMetadata() {
      return getFormMetadata(getClass());
   }

   public FormController getFormController(Object form) {
      return controller;
   }

   public MockFormController getController() {
      return controller;
   }

   public FormMetadata getMetadata() {
      return metadata;
   }

   public Map getContext() {
      return context;
   }

   public void setContext(Map context) {
      this.context = context;
   }

   public Method getMethod(String methodName) {
      try {
         return getClass().getMethod(methodName, new Class[0]);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public void someAction() {
   }

   public List someDataProvider() {
      return Collections.EMPTY_LIST;
   }

   public Object getObjectField() {
      return objectField;
   }

   public void setObjectField(Object objectField) {
      this.objectField = objectField;
   }

   public String getStringField() {
      return stringField;
   }

   public void setStringField(String stringField) {
      this.stringField = stringField;
   }

   public int getIntField() {
      return intField;
   }

   public void setIntField(int intField) {
      this.intField = intField;
   }
}