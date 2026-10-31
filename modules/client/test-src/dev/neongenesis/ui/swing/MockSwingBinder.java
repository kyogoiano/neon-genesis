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
package dev.neongenesis.ui.swing;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ButtonGroup;

import dev.neongenesis.ui.binding.GroupBinder;
import dev.neongenesis.ui.binding.LookupStrategy;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.metadata.ActionMetadata;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FieldMetadata;

public class MockSwingBinder extends SwingBinder {
   private Map map = new HashMap();

   public MockSwingBinder(Component component,
         LookupStrategy lookupStrategy, Object form, Object handler,
         boolean bindDefaultButton) {
      super(component, lookupStrategy, form, handler, bindDefaultButton);
   }

   public MockSwingBinder(Component component,
         LookupStrategy lookupStrategy, Object form, Object handler) {
      super(component, lookupStrategy, form, handler);
   }

   public MockSwingBinder(Component component,
         LookupStrategy lookupStrategy, Object form) {
      super(component, lookupStrategy, form);
   }

   public MockSwingBinder(Component component, Object form, Object handler) {
      super(component, form, handler);
   }

   public MockSwingBinder(Component component, Object form) {
      super(component, form);
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

   public WidgetBinder getWidgetBinder(Component component) {
      return super.getWidgetBinder(component);
   }

   public GroupBinder getGroupBinder(ButtonGroup group) {
      return super.getGroupBinder(group);
   }

   public void invokeFormAction(ActionMetadata actionMetadata) {
      map.put("invokeFormAction(ActionMetadata)", actionMetadata);
   }

   public void populateForm(FieldMetadata fieldMetadata, Object value) {
      map.put("populateForm(FieldMetadata,Object)", value);
   }

   public void updateFormSelection(DataProviderMetadata dataProviderMetadata,
         int[] indexes) {
      map.put("updateFormSelection(DataProviderMetadata,int[])", indexes);
   }

}
