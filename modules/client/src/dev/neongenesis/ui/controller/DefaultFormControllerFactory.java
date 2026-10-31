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

import dev.neongenesis.ui.metadata.DefaultFormMetadataFactory;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.FormMetadataFactory;

public class DefaultFormControllerFactory implements FormControllerFactory {
   private int maximumEvaluationTimes = 1;
   private boolean resetOnDataProviderChange = true;

   public FormController getFormController(Object form) {
      FormController controller = getController(form);
      if (controller == null) {
         controller = createFormController();

         configure(controller, form);
         registerController(form, controller);
      }

      return controller;
   }

   public void setMaximumEvaluationTimes(int maximumEvaluationTimes) {
      this.maximumEvaluationTimes = maximumEvaluationTimes;
   }

   public void setResetOnDataProviderChange(boolean resetOnDataProviderChange) {
      this.resetOnDataProviderChange = resetOnDataProviderChange;
   }

   protected FormController getController(Object form) {
      return FormControllerRegistry.getInstance().get(form);
   }
   
   protected void registerController(Object form, FormController controller) {
      FormControllerRegistry.getInstance().register(form, controller);
   }
   
   protected FormController createFormController() {
      return new DefaultFormController();
   }

   protected void configure(FormController controller, Object form) {
      controller.setForm(form);
      controller.setFormMetadata(getFormMetadata(form));
      controller.setMaximumEvaluationTimes(maximumEvaluationTimes);
      controller.setResetOnDataProviderChange(resetOnDataProviderChange);
   }

   protected FormMetadataFactory createFormMetadataFactory() {
      return new DefaultFormMetadataFactory();
   }

   protected FormMetadata getFormMetadata(final Object form) {
      return createFormMetadataFactory().getFormMetadata(form.getClass());
   }
}
