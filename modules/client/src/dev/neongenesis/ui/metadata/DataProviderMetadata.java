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

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dev.neongenesis.reflection.ClassesCache;
import dev.neongenesis.reflection.FieldEntry;
import dev.neongenesis.script.ScriptExpression;

import dev.neongenesis.util.Bundle;
import org.apache.commons.beanutils.PropertyUtils;

public class DataProviderMetadata {
   private static final List EMPTY_LIST = Collections.EMPTY_LIST;
   private static final int[] EMPTY_INT_ARRAY = new int[0];

   private final String name;
   private String widgetName;
   private FieldEntry objectField;
   private FieldEntry indexField;
   private boolean callOnInit;
   private boolean resetSelection;
   private ScriptExpression clearOnCondition;

   protected DataProviderMetadata(Method method) {
      this.name = method.getName();

      if ((!method.getReturnType().isArray() || method.getReturnType()
            .getComponentType().isPrimitive())
            && !List.class.isAssignableFrom(method.getReturnType())) {
         throw new IllegalArgumentException(Bundle.getMessage(
               DataProviderMetadata.class, "METHOD_X_IS_A_DATAPROVIDER", name)); // NOI18N
      }
   }

   public String getName() {
      return this.name;
   }

   public FieldEntry getObjectField() {
      return objectField;
   }

   public void setObjectField(FieldEntry objectField) {
      this.objectField = objectField;
   }

   public FieldEntry getIndexField() {
      return indexField;
   }

   public String getWidgetName() {
      return widgetName;
   }

   public void setWidgetName(String widgetName) {
      this.widgetName = widgetName;
   }

   public void setIndexField(FieldEntry indexField) {
      this.indexField = indexField;
   }

   public boolean isCallOnInit() {
      return callOnInit;
   }

   public void setCallOnInit(boolean callOnInit) {
      this.callOnInit = callOnInit;
   }

   public boolean isResetSelection() {
      return resetSelection;
   }

   public void setResetSelection(boolean resetSelection) {
      this.resetSelection = resetSelection;
   }

   public ScriptExpression getClearOnCondition() {
      return clearOnCondition;
   }

   public void setClearOnCondition(ScriptExpression clearOnCondition) {
      this.clearOnCondition = clearOnCondition;
   }

   public int[] getSelectedIndexes(final Object indexes) {
      if (indexes == null) {
         return EMPTY_INT_ARRAY;
      }

      if (!indexField.isMultiple()) {
         final int index = ((Integer)indexes).intValue();
         return index < 0 ? EMPTY_INT_ARRAY : new int[] {index};
      }

      if (indexField.isPrimitiveArray()) {
         final int[] intIndexes = (int[]) indexes;
         Arrays.sort(intIndexes);

         return intIndexes;
      }

      if (indexField.isArray()) {
         final Integer[] integerIndexes = (Integer[])indexes;
         final int[] intIndexes = new int[integerIndexes.length];

         for (int i = 0; i < intIndexes.length; i++) {
            intIndexes[i] = integerIndexes[i].intValue();
         }

         Arrays.sort(intIndexes);

         return intIndexes;
      } 

      final Collection colIndexes = (Collection)indexes;
      final int[] intIndexes = new int[colIndexes.size()];
      int i = 0;

      for (Iterator iter = colIndexes.iterator(); iter.hasNext(); i++) {
         intIndexes[i] = ((Integer)iter.next()).intValue();
      }

      Arrays.sort(intIndexes);

      return intIndexes;
   }

   public void resetSelectedFields(final Object target) throws Exception {
      populateSelectedFields(target, EMPTY_LIST, EMPTY_INT_ARRAY);
   }

   public int[] retainSelectedFields(final Object target,
         final List newObjectList, final int[] lastSelectedIndexes)
         throws Exception {

      if (newObjectList.isEmpty()) {
         resetSelectedFields(target);
         return EMPTY_INT_ARRAY;
      }

      Arrays.sort(lastSelectedIndexes);

      int i = 0;
      for (; i < lastSelectedIndexes.length; i++) {
         if (lastSelectedIndexes[i] >= newObjectList.size()) {
            break;
         }
      }

      final int[] selectedIndexes = new int[i];
      if (i > 0) {
         System.arraycopy(lastSelectedIndexes, 0, selectedIndexes, 0, i);
      }

      populateSelectedFields(target, newObjectList, selectedIndexes);

      return selectedIndexes;
   }

   public Object populateSelectedFields(final Object target, 
         final List objectList, final int[] selectedIndexes) throws Exception {
      Object value = null;

      if (objectField != null) {
         value = populateObjectField(target, objectList, selectedIndexes);
      }

      if (indexField != null) {
         populateIndexField(target, selectedIndexes);
      }
      
      return value;
   }

   private Object populateObjectField(final Object target, 
         final List objectList, final int[] selectedIndexes) throws Exception {
      Object value = null;

      if (objectField.isMultiple()) {
         final Object[] values = objectField.isArray() ?
               (Object[])Array.newInstance(
                     ClassesCache.getClass(objectField.getFieldTypeName()).getComponentType(),
                     selectedIndexes.length)
               : new Object[selectedIndexes.length];

         for (int i = 0; i < selectedIndexes.length; i++) {
            values[i] = objectList.get(selectedIndexes[i]);
         }

         value = objectField.isArray() ? (Object)values : Arrays.asList(values);
      } else {
         value = selectedIndexes.length == 0 || selectedIndexes[0] < 0 ?
               null : objectList.get(selectedIndexes[0]);      
      }

      PropertyUtils.setProperty(target, objectField.getFieldName(), value);

      return value;
   }

   private void populateIndexField(final Object target, 
         final int[] selectedIndexes) throws Exception {
      if (indexField.isPrimitiveArray()) {
         PropertyUtils.setProperty(target, indexField.getFieldName(), 
               selectedIndexes);
      } else if (indexField.isMultiple()) {
         final Integer[] integerArray = new Integer[selectedIndexes.length];

         for (int i = 0; i < integerArray.length; i++) {
            integerArray[i] = selectedIndexes[i];
         }

         final Object value = indexField.isArray() ? (Object)integerArray : 
               Arrays.asList(integerArray);
         PropertyUtils.setProperty(target, indexField.getFieldName(), value);
      } else {
         PropertyUtils.setProperty(target, indexField.getFieldName(),
               selectedIndexes.length == 0 ?
                     (indexField.isPrimitive() ? -1 : null) :
                       Integer.valueOf(selectedIndexes[0]));
      }
   }
}