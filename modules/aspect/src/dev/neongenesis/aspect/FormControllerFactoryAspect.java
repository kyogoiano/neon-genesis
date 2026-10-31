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
package dev.neongenesis.aspect;

import dev.neongenesis.ui.controller.DefaultFormControllerFactory;
import dev.neongenesis.ui.controller.FormControllerFactory;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareMixin;

@Aspect
public class FormControllerFactoryAspect {
   @DeclareMixin("@dev.neongenesis.annotation.Form *")
   public static FormControllerFactory createFormControllerFactory(Object form) {
      AspectFormControllerFactory factory = new AspectFormControllerFactory();
      String times = System.getProperty(
            "genesis.aspect.maximumEvaluationTimes");
      if (times != null) {
         factory.setMaximumEvaluationTimes(Integer.parseInt(times));
      }
      factory.setResetOnDataProviderChange(!"false".equals(
            System.getProperty("genesis.aspect.resetOnDataProviderChange",
                  "true")));
      return factory;
   }

   private static class AspectFormControllerFactory
         extends DefaultFormControllerFactory {
   }
}
