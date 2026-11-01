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
package dev.neongenesis.ui;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import dev.neongenesis.util.Bundle;

public class UIUtils {
   private static final UIUtils instance = new UIUtils();
   private ResourceBundle bundle;

   private UIUtils() {
   }
   
   public static UIUtils getInstance() {
      return instance;
   }

   public String getStackTrace(final Throwable throwable) {
      final StringWriter stringWriter = new StringWriter();
      throwable.printStackTrace(new PrintWriter(stringWriter));

      return stringWriter.toString().replaceAll("\t", "   "); // NOI18N
   }

   public ResourceBundle getBundle() {
      if (bundle == null) {
         try {
            bundle = ResourceBundle.getBundle("messages", Locale.getDefault(),  // NOI18N
                  Thread.currentThread().getContextClassLoader());
         } catch (MissingResourceException e) {
            throw new RuntimeException(Bundle.getMessage(UIUtils.class,
                  "MESSAGE_FILE_NOT_FOUND_CLASSPATH")); // NOI18N
         }
      }

      return bundle;
   }
}