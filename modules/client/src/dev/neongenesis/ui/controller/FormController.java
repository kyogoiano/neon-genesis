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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;

public interface FormController {
   public static final String FORM_METADATA_KEY = "genesis:formMetadata"; // NOI18N
   public static final String CURRENT_STATE_KEY = "genesis:currentState"; // NOI18N

   public void setForm(Object form);
   public void setFormMetadata(FormMetadata form);
   public Object getForm();
   public FormMetadata getFormMetadata();

   public void setMaximumEvaluationTimes(int times);
   public int getMaximumEvaluationTimes();

   public void setResetOnDataProviderChange(boolean resetOnDataProviderChange);
   public boolean isResetOnDataProviderChange();

   public void addFormControllerListener(FormControllerListener listener);
   public boolean removeFormControllerListener(FormControllerListener listener);
   public Collection getFormControllerListeners();
   public void fireAllEvents(FormControllerListener listener) throws Exception;

   public void setup() throws Exception;
   public boolean isSetup();

   public void populate(Map properties, Map converters) throws Exception;
   public void invokeAction(String actionName, Map stringProperties) 
         throws Exception;
   public void updateSelection(DataProviderMetadata dataProviderMetadata, 
         int[] selected) throws Exception;
   public void resetSelection(DataProviderMetadata dataProviderMetadata,
         List dataProvided) throws Exception;
   public void update() throws Exception;
   public FormState getFormState() throws Exception;
   public void reset(FormState state) throws Exception;
   public ScriptContext getScriptContext();
}