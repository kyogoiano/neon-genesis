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
package dev.neongenesis.script;

import dev.neongenesis.reflection.ClassesCache;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

public class ScriptRegistry {
   public static final String JXPATH = "jxpath"; // NOI18N
   public static final String JAVASCRIPT = "javascript"; // NOI18N
   public static final String BEANSHELL = "beanshell"; // NOI18N
   public static final String EL = "el"; // NOI18N

   private static final ScriptRegistry instance = new ScriptRegistry();

   private final Map registry = new HashMap(4);
   private final Map scriptsRegistry = new HashMap(1);

   private String scriptFactoryName = JAVASCRIPT;
   private Map scriptFactoryProperties;
   private ScriptFactory scriptFactory;

   private ScriptRegistry() {
      registry.put(JXPATH,
            "dev.neongenesis.script.jxpath.JXPathScriptFactory"); // NOI18N
      // BeanShell via its own BSFScriptFactory wrapper is kept for backwards
      // compatibility; it does not pull in the BSF jar because BSFScriptFactory
      // was replaced by a direct-Rhino implementation and beanshell has its own
      // BshScriptEngineFactory (JSR-223) registered automatically.
      registry.put(BEANSHELL,
            "dev.neongenesis.script.javaxscript.JavaxScriptScriptFactory"); // NOI18N
      registry.put(EL, "dev.neongenesis.script.el.ELScriptFactory"); // NOI18N
      // JavaScript uses Mozilla Rhino 1.8 via JSR-223 (rhino-engine artifact).
      // BSF was removed; rhino-engine registers itself under "javascript" and
      // "rhino" so no BSF bridge is needed.  This path works on JDK 11–21+.
      registry.put(JAVASCRIPT,
            "dev.neongenesis.script.javaxscript.JavaxScriptScriptFactory"); // NOI18N
   }

   public static ScriptRegistry getInstance() {
      return instance;
   }

   public void setScriptFactoryName(String name) {
      this.scriptFactoryName = name;
   }
   
   protected String getScriptFactoryName() {
      return scriptFactoryName;
   }

   public void setScriptFactoryProperties(Map scriptFactoryProperties) {
      this.scriptFactoryProperties = scriptFactoryProperties;
   }

   public void initialize() {
      scriptFactory = initialize(getScriptFactoryName(), scriptFactoryProperties);
   }

   public ScriptFactory initialize(String alias, Map properties) {
      String className = getClassNameFromAlias(alias);

      // doesn't need to use equals method
      if (alias != className) {
         if (properties == null) {
            properties = new HashMap();
         }

         properties.put("lang", alias); // NOI18N
      }

      ScriptFactory factory = (ScriptFactory) newInstance(className);

      if (properties != null && !properties.isEmpty()) {
         copyProperties(factory, properties);
      }

      return factory;
   }

   protected String getClassNameFromAlias(String alias) {
      String className = (String) registry.get(alias);

      if (className != null) {
         return className;
      }

      return alias;
   }

   protected Object newInstance(String className) {
      try {
         return ClassesCache.getClass(className).getDeclaredConstructor()
               .newInstance();
      } catch (ReflectiveOperationException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   protected void copyProperties(Object dest, Object source) {
      try {
         PropertyUtils.copyProperties(dest, source);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (InvocationTargetException e) {
         throw new RuntimeException(e.getMessage(), e);
      } catch (NoSuchMethodException e) {
         throw new RuntimeException(e.getMessage(), e);
      }
   }

   public boolean isInitialized() {
      return scriptFactory != null;
   }

   public Script getScript(String alias) {
      String className = getClassNameFromAlias(alias);
      Script script = (Script) scriptsRegistry.get(className);
      if (script == null) {
         if (isCurrentScriptFactoryNameFor(alias)) {
            if (!isInitialized()) {
               initialize();
            }

            script = scriptFactory.newScript();
         } else {
            ScriptFactory factory = initialize(alias, null);
            script = factory.newScript();
         }

         scriptsRegistry.put(className, script);
      }

      return script;
   }

   public Script getScript() {
      return getScript(getScriptFactoryName());
   }

   public boolean isCurrentScriptFactoryNameFor(String alias) {
      if (getScriptFactoryName().equals(alias)) {
         return true;
      }

      return getScriptFactoryName().equals(registry.get(alias));
   }
}
