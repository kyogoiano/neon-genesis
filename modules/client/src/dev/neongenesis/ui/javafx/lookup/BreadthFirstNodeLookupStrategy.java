/*
 * Neon Genesis
 * Copyright (C) 2026 Leandro Cardoso de Andrade (@kyogoiano <leandro.ueg@gmail.com>)
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
package dev.neongenesis.ui.javafx.lookup;

import dev.neongenesis.ui.binding.RecursiveLookupStrategy;
import dev.neongenesis.ui.javafx.JavaFXBinder;
import java.util.ArrayDeque;
import java.util.Queue;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Looks up JavaFX {@link Node} instances breadth-first by {@link Node#getId()}
 * or explicit registration in {@link RecursiveLookupStrategy}.
 */
public class BreadthFirstNodeLookupStrategy extends RecursiveLookupStrategy {

   @Override
   public String getName(Object object) {
      String name = super.getName(object);
      if (name != null) {
         return name;
      }
      return getRealName(object);
   }

   @Override
   protected String getRealName(Object object) {
      if (object instanceof Node node) {
         return node.getId();
      }
      return null;
   }

   @Override
   protected boolean isAlreadyBound(Object object) {
      return object instanceof Node node
            && Boolean.TRUE.equals(node.getProperties().get(JavaFXBinder.GENESIS_BOUND));
   }

   @Override
   protected Object lookupImpl(Object root, String name) {
      if (name == null || !(root instanceof Node rootNode)) {
         return null;
      }

      Queue<Node> queue = new ArrayDeque<>();
      queue.add(rootNode);

      while (!queue.isEmpty()) {
         Node current = queue.poll();
         if (name.equals(current.getId())) {
            registerMap(name, current);
            return current;
         }
         if (current instanceof Parent parent) {
            queue.addAll(parent.getChildrenUnmodifiable());
         }
      }

      return null;
   }
}
