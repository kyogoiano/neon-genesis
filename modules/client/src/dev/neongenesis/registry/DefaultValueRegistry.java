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
package dev.neongenesis.registry;

public class DefaultValueRegistry extends Registry {
   public static final DefaultValueRegistry instance = new DefaultValueRegistry();

   private DefaultValueRegistry() {
      register(Boolean.TYPE, Boolean.FALSE);
      register(Byte.TYPE, (byte) 0);
      register(Short.TYPE, (short) 0);
      register(Integer.TYPE, 0);
      register(Long.TYPE, 0L);
      register(Float.TYPE, (float) 0);
      register(Double.TYPE, (double) 0);
   }

   public static final DefaultValueRegistry getInstance() {
      return instance;
   }
}