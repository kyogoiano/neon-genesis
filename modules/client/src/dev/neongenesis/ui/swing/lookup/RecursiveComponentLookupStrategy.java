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
package dev.neongenesis.ui.swing.lookup;

import java.awt.Component;

import javax.swing.JComponent;

import dev.neongenesis.ui.binding.RecursiveLookupStrategy;
import dev.neongenesis.ui.swing.SwingBinder;

public abstract class RecursiveComponentLookupStrategy extends RecursiveLookupStrategy {
   public String getName(Object object) {
      String name = super.getName(object);

      if (name != null) {
         return name;
      }

      return getRealName(object);
   }

   protected String getRealName(Object object) {
      if (object instanceof Component) {
         return ((Component)object).getName();
      }

      return null;
   }

   protected boolean isAlreadyBound(Object object) {
      return object instanceof JComponent
            && (Boolean.TRUE.equals(((JComponent) object)
                  .getClientProperty(SwingBinder.GENESIS_BOUND)));
   }

   protected Object lookupImpl(Object object, String name) {
      if (object instanceof Component) {
         return lookupImpl((Component) object, name);
      }

      return null;
   }

   protected abstract Component lookupImpl(Component component, String name);
}
