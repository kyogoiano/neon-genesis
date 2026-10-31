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

import java.util.List;
import java.util.Map;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.MethodMetadata;

public interface FormControllerListener {
   public void enabledConditionsChanged(Map updatedEnabledConditions);
   public void visibleConditionsChanged(Map updatedVisibleConditions);
   public boolean beforeInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception;
   public void afterInvokingMethod(MethodMetadata methodMetadata) 
         throws Exception;
   public void dataProvidedListChanged(DataProviderMetadata metadata, 
         List items) throws Exception;
   public void dataProvidedIndexesChanged(DataProviderMetadata metadata,
         int[] selectedIndexes);
   public void valuesChanged(Map updatedValues) throws Exception;
}