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
package dev.neongenesis.ui.binding;

import java.util.HashMap;
import java.util.Map;

import dev.neongenesis.mockobjects.MockBoundAction;
import dev.neongenesis.mockobjects.MockBoundDataProvider;
import dev.neongenesis.mockobjects.MockBoundField;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;

public class MockAbstractBinder extends AbstractBinder {
   private Map map = new HashMap();

   public MockAbstractBinder(Object root, Object form, Object handler) {
      super(root, form, handler, null);
   }

   public Object get(Object key) {
      return map.get(key);
   }

   public void put(Object key, Object value) {
      map.put(key, value);
   }

   public void clear() {
      map.clear();
   }

   protected BoundAction bindActionMetadata(String name,
         FormMetadata formMetadata, ActionMetadata actionMetadata) {
      BoundAction bound = new MockBoundAction(name);
      map.put("bindActionMetadata(String,FormMetadata,ActionMetadata)", bound);
      return bound;
   }

   protected BoundDataProvider bindDataProvider(String name,
         FormMetadata formMetadata, DataProviderMetadata dataProviderMetadata) {
      BoundDataProvider bound = new MockBoundDataProvider(name);
      map.put("bindDataProvider(String,FormMetadata,DataProviderMetadata)",
            bound);
      return bound;
   }

   protected BoundField bindFieldMetadata(String name,
         FormMetadata formMetadata, FieldMetadata fieldMetadata) {
      BoundField bound = new MockBoundField(name);
      map.put("bindFieldMetadata(String,FormMetadata,FieldMetadata)", bound);
      return bound;
   }

   public void handleException(Throwable throwable) {
      map.put("handleException(Throwable)", throwable);
   }

   protected ExceptionHandler createExceptionHandler() {
      ExceptionHandler handler = new MockExceptionHandler();
      map.put("createExceptionHandler()", handler);
      return handler;
   }

   public boolean isVirtual(Object widget) {
      map.put("isVirtual(Object)", widget);
      return false;
   }

   public String getName(Object object) {
      map.put("getName(Object)", object);
      return object == null ? "null" : object.toString();
   }

   protected LookupStrategy createLookupStrategy() {
      return null;
   }

   protected GroupBinder getDefaultGroupBinderFor(Object group) {
      return null;
   }

   protected WidgetBinder getDefaultWidgetBinderFor(Object widget) {
      return null;
   }

   protected void markBound() {
   }
   
   protected void markUnbound() {
   }
}
