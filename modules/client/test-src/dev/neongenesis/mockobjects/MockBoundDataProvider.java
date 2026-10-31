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
package dev.neongenesis.mockobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.neongenesis.ui.binding.BoundDataProvider;

public class MockBoundDataProvider implements BoundDataProvider {
   private String name;
   private Map map = new HashMap();

   public MockBoundDataProvider(String name) {
      this.name = name;
   }
   
   public Object get(Object key) {
      return map.get(key);
   }

   public void put(Object key, Object value) {
      map.put(key, value);
   }
   
   public void clear() {
      map.clear();
   }

   public void updateIndexes(int[] indexes) {
      map.put("updateIndexes(int[])", indexes);
   }

   public void updateList(List data) throws Exception {
      map.put("updateList(List)", data);
   }

   public String getName() {
      return name;
   }

   public void unbind() {
      map.put("unbind()", Boolean.TRUE);
   }
}
