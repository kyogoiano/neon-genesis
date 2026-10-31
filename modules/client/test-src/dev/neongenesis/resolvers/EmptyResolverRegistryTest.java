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
import java.util.HashMap;
import java.util.Map;

import dev.neongenesis.GenesisTestCase;

public class EmptyResolverRegistryTest extends GenesisTestCase {
   public void testGetDefaultEmptyResolverForClass() {
      assertEquals(DefaultEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(Object.class).getClass());
      assertEquals(StringEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(String.class).getClass());
      assertEquals(CollectionEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(Collection.class).getClass());
      assertEquals(MapEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(Map.class).getClass());
   }

   public void testGetDefaultEmptyResolverForClassMap() {
      assertEquals(StringEmptyResolver.class, EmptyResolverRegistry
            .getInstance().getDefaultEmptyResolverFor(String.class, null)
            .getClass());

      Map attributes = new HashMap();
      attributes.put("trim", "false");

      assertFalse(((StringEmptyResolver)EmptyResolverRegistry.getInstance()
            .getDefaultEmptyResolverFor(String.class, attributes)).isTrim());
   }

   public void testGetEmptyResolver() {
      Map attributes = new HashMap();
      attributes.put("trim", "false");

      assertFalse(((StringEmptyResolver)EmptyResolverRegistry.getInstance()
            .getEmptyResolver(StringEmptyResolver.class.getName(), attributes))
            .isTrim());
   }
}