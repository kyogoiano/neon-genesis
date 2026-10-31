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

import dev.neongenesis.commons.jxpath.ri.axes.ChildContext;

import dev.neongenesis.util.Bundle;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.EvalContext;
import org.apache.commons.jxpath.ri.QName;
import org.apache.commons.jxpath.ri.axes.AncestorContext;
import org.apache.commons.jxpath.ri.axes.AttributeContext;
import org.apache.commons.jxpath.ri.axes.DescendantContext;
import org.apache.commons.jxpath.ri.axes.NamespaceContext;
import org.apache.commons.jxpath.ri.axes.ParentContext;
import org.apache.commons.jxpath.ri.axes.PrecedingOrFollowingContext;
import org.apache.commons.jxpath.ri.axes.SelfContext;
import org.apache.commons.jxpath.ri.compiler.NodeNameTest;
import org.apache.commons.jxpath.ri.compiler.NodeTest;
import org.apache.commons.jxpath.ri.compiler.Step;

public class LocationPath extends
      org.apache.commons.jxpath.ri.compiler.LocationPath {

   public LocationPath(boolean absolute, Step[] steps) {
      super(absolute, steps);
   }

   protected EvalContext createContextForStep(EvalContext context, int axis,
         NodeTest nodeTest) {
      if (nodeTest instanceof NodeNameTest) {
         QName qname = ((NodeNameTest) nodeTest).getNodeName();
         String prefix = qname.getPrefix();
         if (prefix != null) {
            String namespaceURI = context.getJXPathContext().getNamespaceURI(
                  prefix);
            nodeTest = new NodeNameTest(qname, namespaceURI);
         }
      }

      switch (axis) {
         case Compiler.AXIS_ANCESTOR:
            return new AncestorContext(context, false, nodeTest);
         case Compiler.AXIS_ANCESTOR_OR_SELF:
            return new AncestorContext(context, true, nodeTest);
         case Compiler.AXIS_ATTRIBUTE:
            return new AttributeContext(context, nodeTest);
         case Compiler.AXIS_CHILD:
            return new ChildContext(context, nodeTest, false, false);
         case Compiler.AXIS_DESCENDANT:
            return new DescendantContext(context, false, nodeTest);
         case Compiler.AXIS_DESCENDANT_OR_SELF:
            return new DescendantContext(context, true, nodeTest);
         case Compiler.AXIS_FOLLOWING:
            return new PrecedingOrFollowingContext(context, nodeTest, false);
         case Compiler.AXIS_FOLLOWING_SIBLING:
            return new ChildContext(context, nodeTest, true, false);
         case Compiler.AXIS_NAMESPACE:
            return new NamespaceContext(context, nodeTest);
         case Compiler.AXIS_PARENT:
            return new ParentContext(context, nodeTest);
         case Compiler.AXIS_PRECEDING:
            return new PrecedingOrFollowingContext(context, nodeTest, true);
         case Compiler.AXIS_PRECEDING_SIBLING:
            return new ChildContext(context, nodeTest, true, true);
         case Compiler.AXIS_SELF:
            return new SelfContext(context, nodeTest);
      }
      
      throw new IllegalArgumentException(Bundle.getMessage(LocationPath.class,
            "X_IS_NOT_A_VALID_VALUE_FOR_AXIS", Integer.toString(axis))); // NOI18N
   }
}