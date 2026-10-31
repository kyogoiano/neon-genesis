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

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import dev.neongenesis.GenesisTestCase;

public class FormatAdapterTest extends GenesisTestCase {
   private static final class MockFormat extends Format {
      private String formattedString;

      void setFormattedString(String formattedString) {
         this.formattedString = formattedString;
      }

      public StringBuffer format(Object obj, StringBuffer toAppendTo, 
            FieldPosition pos) {
         return toAppendTo.append(formattedString);
      }
      
      public Object parseObject(String source, ParsePosition pos) {
         throw new UnsupportedOperationException();
      }
   }

   public void testFormatConstructor() {
      MockFormat mock = new MockFormat();
      FormatAdapter adapter = new FormatAdapter(mock);
      testFormat(adapter, mock, false);
   }

   public void testFormatTrueConstructor() {
      MockFormat mock = new MockFormat();
      FormatAdapter adapter = new FormatAdapter(mock, true);
      testFormat(adapter, mock, true);
   }

   public void testFormatFalseConstructor() {
      MockFormat mock = new MockFormat();
      FormatAdapter adapter = new FormatAdapter(mock, false);
      testFormat(adapter, mock, false);
   }

   private void testFormat(FormatAdapter formatter, MockFormat mock, 
         boolean nullAsBlank) {
      String text = "text";
      mock.setFormattedString(text);

      assertEquals(formatter.format(null), nullAsBlank ? "" : text);
      assertEquals(formatter.format(new Object()), text);
   }
}