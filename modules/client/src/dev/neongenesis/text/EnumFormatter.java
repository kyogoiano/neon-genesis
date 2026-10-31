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
package dev.neongenesis.text;

import java.util.MissingResourceException;

import dev.neongenesis.helpers.EnumHelper;
import dev.neongenesis.ui.UIUtils;
import org.apache.commons.logging.LogFactory;

public class EnumFormatter implements Formatter {
   public String format(final Object o) {
      if (o == null) {
         return ""; // NOI18N
      }

      String className = EnumHelper.getInstance().getDeclaringClass(o).getName();
      className = className.substring(className.lastIndexOf('.') + 1);
      className = className.substring(className.lastIndexOf('$') + 1);

      final String key = className + '.' + EnumHelper.getInstance().getName(o);

      try {
         return UIUtils.getInstance().getBundle().getString(key);
      } catch (MissingResourceException mre) {
         LogFactory.getLog(getClass()).info("Not found: " + key); // NOI18N
         return EnumHelper.getInstance().getName(o);
      }
   }
}