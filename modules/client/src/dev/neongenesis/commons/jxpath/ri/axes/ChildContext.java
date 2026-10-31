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
package dev.neongenesis.commons.jxpath.ri.axes;

import dev.neongenesis.commons.jxpath.GenesisNodeSet;

import dev.neongenesis.util.Bundle;
import org.apache.commons.jxpath.JXPathException;
import org.apache.commons.jxpath.NodeSet;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.compiler.NodeTest;

public class ChildContext extends
      org.apache.commons.jxpath.ri.axes.ChildContext {
   private final String path;

   public ChildContext(EvalContext parentContext, NodeTest nodeTest,
         boolean startFromParentLocation, boolean reverse) {
      super(parentContext, nodeTest, startFromParentLocation, reverse);
      if (parentContext instanceof ChildContext) {
         path = ((ChildContext) parentContext).getPath() + "/" // NOI18N
               + nodeTest.toString();
      } else {
         path = nodeTest.toString();
      }
   }

   public NodeSet getNodeSet() {
      if (position != 0) {
         throw new JXPathException(Bundle.getMessage(ChildContext.class,
               "SIMULTANEOUS_OPERATIONS_WARNING")); // NOI18N
      }
      GenesisNodeSet set = new GenesisNodeSet(getPath());
      while (nextSet()) {
         while (nextNode()) {
            set.add((Pointer) getCurrentNodePointer().clone());
         }
      }
      return set;
   }

   public String getPath() {
      return path;
   }

}