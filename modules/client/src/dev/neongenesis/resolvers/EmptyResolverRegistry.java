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
package dev.neongenesis.resolvers;

import java.util.Collection;
import java.util.Map;

import dev.neongenesis.registry.RegistryFactory;


public class EmptyResolverRegistry extends RegistryFactory {
	private static final EmptyResolverRegistry instance = new EmptyResolverRegistry();

	private EmptyResolverRegistry() {
      register(Object.class, new DefaultEmptyResolver());
      register(String.class, new StringEmptyResolver());
      register(Collection.class, new CollectionEmptyResolver());
      register(Map.class, new MapEmptyResolver());
   }

   public static EmptyResolverRegistry getInstance() {
      return instance;
   }

   public EmptyResolver getEmptyResolver(final String resolverClassName,
         final Map attributesMap) {
      return (EmptyResolver)getNewInstance(resolverClassName, attributesMap);
   }

   public EmptyResolver getDefaultEmptyResolverFor(final Class clazz) {
      return getDefaultEmptyResolverFor(clazz, (Map)null);
   }

   public EmptyResolver getDefaultEmptyResolverFor(final Class clazz,
         final Map attributesMap) {
      return (EmptyResolver)getExistingInstance(clazz, attributesMap);
   }

}