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

import java.util.Comparator;

public class EqualityComparatorAdapter implements EqualityComparator, Comparator {
   private final Comparator comparator;

   public EqualityComparatorAdapter(final Comparator comparator) {
      this.comparator = comparator;
   }
   
   public boolean equals(Object o1, Object o2) {
      return comparator.compare(o1, o2) == 0;
   }
   
   public int compare(Object o1, Object o2) {
      return comparator.compare(o1, o2);
   }
}