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
package dev.neongenesis.ui.controller;

import java.util.HashMap;
import java.util.Map;

public class FormStateImpl implements FormState {
   private final Map enabledMap = new HashMap();
   private final Map visibleMap = new HashMap();
   private final Map changedMap = new HashMap();
   private final Map valuesMap = new HashMap();
   private final Map dataProvidedMap = new HashMap();
   private final Map dataProvidedIndexesMap = new HashMap();

   public FormStateImpl() {
   }

   public FormStateImpl(FormState state) {
      this(state, state.getValuesMap());
   }

   public FormStateImpl(FormState state, Map values) {
      enabledMap.putAll(state.getEnabledMap());
      visibleMap.putAll(state.getVisibleMap());
      changedMap.putAll(state.getChangedMap());
      valuesMap.putAll(values);
      dataProvidedMap.putAll(state.getDataProvidedMap());
      dataProvidedIndexesMap.putAll(state.getDataProvidedIndexesMap());
   }

   public Map getChangedMap() {
      return changedMap;
   }

   public Map getEnabledMap() {
      return enabledMap;
   }

   public Map getVisibleMap() {
      return visibleMap;
   }

   public Map getValuesMap() {
      return valuesMap;
   }

   public Map getDataProvidedMap() {
      return dataProvidedMap;
   }

   public Map getDataProvidedIndexesMap() {
      return dataProvidedIndexesMap;
   }

   public String toString() {
      return getClass().getName() + 
            ": [enabledMap: " + enabledMap + // NOI18N
            "; visibleMap: " + visibleMap + // NOI18N
            "; changedMap: " + changedMap + // NOI18N
            "; valuesMap: " + valuesMap + // NOI18N
            "; dataProvidedMap: " + dataProvidedMap + // NOI18N
            "; dataProvidedIndexesMap: " + dataProvidedIndexesMap + "]"; // NOI18N
   }
}