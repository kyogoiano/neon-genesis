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


public abstract class RecursiveLookupStrategy extends MapLookupStrategy {
   private boolean skipLookupInBoundComponent = true;

   public boolean isSkipLookupInBoundComponent() {
      return skipLookupInBoundComponent;
   }

   public void setSkipLookupInBoundComponent(boolean skipGenesisBoundComponents) {
      this.skipLookupInBoundComponent = skipGenesisBoundComponents;
   }

   public Object lookup(Object object, String name) {
      if (object == null || name == null) {
         return null;
      }

      if (doSkip(object)) {
         return null;
      }

      Object result = super.lookup(object, name);

      if (result != null) {
         return result;
      }

      if (name.equals(getRealName(object))) {
         return object;
      }

      return lookupImpl(object, name);
   }

   protected boolean doSkip(Object object) {
      return skipLookupInBoundComponent && isAlreadyBound(object);
   }

   protected abstract String getRealName(Object object);
   protected abstract boolean isAlreadyBound(Object object);
   protected abstract Object lookupImpl(Object object, String name);
}
