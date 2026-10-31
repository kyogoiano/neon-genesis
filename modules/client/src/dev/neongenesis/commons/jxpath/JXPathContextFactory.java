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
package dev.neongenesis.commons.jxpath;

import dev.neongenesis.commons.jxpath.ri.JXPathContextImpl;
import dev.neongenesis.commons.jxpath.util.GenesisTypeConverter;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathContextFactoryConfigurationError;
import org.apache.commons.jxpath.util.TypeUtils;

public class JXPathContextFactory extends
      org.apache.commons.jxpath.JXPathContextFactory {
   
   static {
      TypeUtils.setTypeConverter(new GenesisTypeConverter());
   }

   public JXPathContext newContext(JXPathContext parentContext,
         Object contextBean) throws JXPathContextFactoryConfigurationError {
      return new JXPathContextImpl(parentContext, contextBean);
   }

}