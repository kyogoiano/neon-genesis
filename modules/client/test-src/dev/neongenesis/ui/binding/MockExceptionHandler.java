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
package dev.neongenesis.ui.binding;

import java.util.HashMap;
import java.util.Map;

public class MockExceptionHandler extends AbstractDispatcherExceptionHandler {
   private Map map = new HashMap();

   public Object get(Object key) {
      return map.get(key);
   }

   public void put(Object key, Object value) {
      map.put(key, value);
   }

   public void clear() {
      map.clear();
   }

   public void handleException(Throwable t) {
      put("handleException(Throwable)", t);
   }

   protected void showErrorMessageDialog(String message, String title,
         Throwable throwable) {
   }

   protected void showWarningMessageDialog(String message, String title) {
   }
}
