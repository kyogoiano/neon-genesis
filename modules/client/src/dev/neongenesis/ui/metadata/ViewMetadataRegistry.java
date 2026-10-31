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
package dev.neongenesis.ui.metadata;

import java.util.HashMap;
import java.util.Map;

public class ViewMetadataRegistry {
   private static final ViewMetadataRegistry instance = new ViewMetadataRegistry();

   private final Map registry = new HashMap();

   private ViewMetadataRegistry() {
   }

   public static ViewMetadataRegistry getInstance() {
      return instance;
   }

   public ViewMetadata get(Class clazz) {
      return (ViewMetadata) registry.get(clazz);
   }

   public ViewMetadata register(Class clazz, ViewMetadata meta) {
      return (ViewMetadata) registry.put(clazz, meta);
   }
}
