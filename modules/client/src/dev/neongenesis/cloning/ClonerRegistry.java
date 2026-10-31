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
package dev.neongenesis.cloning;

import java.util.Map;

import dev.neongenesis.registry.RegistryFactory;

public final class ClonerRegistry {
   private static final ClonerRegistry instance = new ClonerRegistry();
	private final RegistryFactory factory = new RegistryFactory();

   private ClonerRegistry() {
      register(Object.class, new DefaultCloner());
   }

   public static ClonerRegistry getInstance() {
      return instance;
   }

   public Cloner getCloner(final String className,
         final Map attributesMap) {
      return (Cloner)factory.getNewInstance(className, attributesMap);
   }

   public Cloner getDefaultClonerFor(final Class clazz) {
      return getDefaultClonerFor(clazz, null);
   }

   public Cloner getDefaultClonerFor(final Class clazz, final Map attributesMap) {
      return (Cloner)factory.getExistingInstance(clazz, attributesMap);
   }

   public Cloner register(final Class clazz, final Cloner cloner) {
      return (Cloner)factory.register(clazz, cloner);
   }
}
