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
package dev.neongenesis.equality;

import java.math.BigDecimal;
import java.util.Map;

import dev.neongenesis.registry.RegistryFactory;

public final class EqualityComparatorRegistry {
   private static final EqualityComparatorRegistry instance = new EqualityComparatorRegistry();
   private final RegistryFactory factory = new RegistryFactory();

   private EqualityComparatorRegistry() {
      factory.register(Object.class, new DefaultEqualityComparator());
      factory.register(String.class, new StringEqualityComparator());
      factory.register(BigDecimal.class, new BigDecimalEqualityComparator());
   }

   public static EqualityComparatorRegistry getInstance() {
      return instance;
   }

   public EqualityComparator getEqualityComparator(final String compClassName, final Map attributesMap) {
      return (EqualityComparator) factory.getNewInstance(compClassName, attributesMap);
   }

   public EqualityComparator getDefaultEqualityComparatorFor(final Class clazz) {
      return getDefaultEqualityComparatorFor(clazz, null);
   }

   public EqualityComparator getDefaultEqualityComparatorFor(final Class clazz, final Map attributesMap) {
      return (EqualityComparator) factory.getExistingInstance(clazz, attributesMap);
   }
}