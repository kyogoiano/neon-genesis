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
package dev.neongenesis.commons.jxpath.ri.compiler;

import org.apache.commons.jxpath.ri.compiler.Step;

public class TreeCompiler extends
      org.apache.commons.jxpath.ri.compiler.TreeCompiler {

   public Object locationPath(boolean absolute, Object[] steps) {
      return new LocationPath(absolute, toStepArray(steps));
   }

   private Step[] toStepArray(Object[] array) {
      Step stepArray[] = null;
      if (array != null) {
         stepArray = new Step[array.length];
         for (int i = 0; i < stepArray.length; i++) {
            stepArray[i] = (Step) array[i];
         }
      }
      return stepArray;
   }
}