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

import java.util.Map;

import dev.neongenesis.script.ScriptRegistry;
import dev.neongenesis.ui.metadata.DefaultFormMetadataFactory;
import dev.neongenesis.ui.metadata.FormMetadataFactory;
import dev.neongenesis.util.GenesisUtils;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareMixin;

@Aspect
public class FormMetadataFactoryAspect {
   @DeclareMixin("@dev.neongenesis.annotation.Form *")
   public static FormMetadataFactory createFormMetadataFactory(Object form) {
      String scriptFactoryName = System.getProperty(
            "genesis.aspect.scriptFactory");
      if (scriptFactoryName != null) {
         ScriptRegistry.getInstance().setScriptFactoryName(scriptFactoryName);
      }

      String properties = System.getProperty(
            "genesis.aspect.scriptFactoryProperties");
      if (properties != null) {
         Map scriptProperties = GenesisUtils.getAttributesMap(properties, ",", "=");
         ScriptRegistry.getInstance().setScriptFactoryProperties(scriptProperties);
      }
      ScriptRegistry.getInstance().initialize();
      return new DefaultFormMetadataFactory();
   }
}
