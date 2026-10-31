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
package dev.neongenesis.commons.jxpath.ri;

import dev.neongenesis.commons.jxpath.ri.compiler.TreeCompiler;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.jxpath.ri.Compiler;
import org.apache.commons.jxpath.ri.JXPathContextReferenceImpl;

public class JXPathContextImpl extends JXPathContextReferenceImpl {
   private static final Compiler COMPILER = new TreeCompiler();

   public JXPathContextImpl(JXPathContext parentContext, Object contextBean) {
      super(parentContext, contextBean);
   }

   public JXPathContextImpl(JXPathContext parentContext, Object contextBean,
         Pointer contextPointer) {
      super(parentContext, contextBean, contextPointer);
   }

   protected Compiler getCompiler() {
      return COMPILER;
   }
}