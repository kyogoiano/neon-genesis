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
package dev.neongenesis;

import junit.framework.TestCase;
import dev.neongenesis.ui.UIUtils;

public abstract class GenesisTestCase extends TestCase {
   public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];
   public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

   public GenesisTestCase() {
   }

   public GenesisTestCase(String name) {
      super(name);
   }

   public void putInResourceBundle(String key, Object value) {
      ((PropertiesProvider)UIUtils.getInstance().getBundle()).getProperties()
            .put(key, value);
   }
}