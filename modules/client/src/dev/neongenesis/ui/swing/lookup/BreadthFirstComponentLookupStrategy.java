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
import java.awt.Container;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JMenu;

public class BreadthFirstComponentLookupStrategy
      extends RecursiveComponentLookupStrategy {
   protected Component lookupImpl(Component component, String name) {
      return breadthFirstLookup(component, name, new ArrayList());
   }

   private Component breadthFirstLookup(Component component, String name,
      List queue) {
      if (component instanceof Container) {
         Container container = (Container) component;

         if (component instanceof JMenu) {
            container = ((JMenu) container).getPopupMenu();
         }

         queue.addAll(Arrays.asList(container.getComponents()));

         while (!queue.isEmpty()) {
            Component first = (Component) queue.remove(0);

            if (name.equals(first.getName())) {
               return first;
            }

            registerMap(first.getName(), first);

            Component c = breadthFirstLookup(first, name, queue);

            if (c != null) {
               return c;
            }
         }
      }

      return null;
   }
}
