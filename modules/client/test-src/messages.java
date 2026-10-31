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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import dev.neongenesis.PropertiesProvider;

/*
 * As scary as it may seem, this class is needed for methods that use a
 * ResourceBundle...
 */
public class messages extends ResourceBundle implements PropertiesProvider {
   private final Map properties = new HashMap();

   public Enumeration getKeys() {
      return Collections.enumeration(properties.keySet());
   }
   
   public Object handleGetObject(String key) {
      Object value = properties.get(key);

      if (value == null) {
         throw new MissingResourceException("Could not be found", 
               getClass().getName(), key);
      }

      return value;
   }

   public Map getProperties() {
      return properties;
   }
}