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
package dev.neongenesis.tests.ui;

import dev.neongenesis.annotation.AfterAction;
import dev.neongenesis.annotation.BeforeAction;
import dev.neongenesis.annotation.ViewHandler;
import dev.neongenesis.tests.TestCase;
import dev.neongenesis.ui.metadata.ViewMetadata;

public class ViewMetadataFactoryTest extends TestCase {

   @ViewHandler
   public static class ValidViewHandler {
      static final String SIMPLE_METHOD = "simpleMethod";
      static final String ALIASED_METHOD = "aliasedMethod";
      static final String ARRAY_METHOD = "arrayMethod";
      String value;

      @BeforeAction
      @AfterAction
      public void simpleMethod() {
         value = SIMPLE_METHOD;
      }

      @BeforeAction("anotherName")
      @AfterAction("anotherName")
      public void aliasedMethod() {
         value = ALIASED_METHOD;
      }

      @BeforeAction({"a", "b"})
      @AfterAction({"a", "b"})
      public void arrayMethod() {
         value = ARRAY_METHOD;
      }
   }

   @ViewHandler
   public static class InvalidAfterActionViewHandler {
      @AfterAction
      public void invalidAfterAction(String arg) {
      }
   }

    @ViewHandler
   public static class InvalidBeforeActionViewHandler {
      @BeforeAction
      public void invalidBeforeAction(String arg) {
      }
   }

   public void testAfterAction() throws Exception {
      ValidViewHandler valid = new ValidViewHandler();
      ViewMetadata validMetadata = getViewMetadata(valid);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "non-existant");
      assertNull(valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, ValidViewHandler.SIMPLE_METHOD);
      assertSame(ValidViewHandler.SIMPLE_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "anotherName");
      assertSame(ValidViewHandler.ALIASED_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "a");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeAfterAction(valid, "b");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      try {
         getViewMetadata(new InvalidAfterActionViewHandler());
         fail("An IllegalArgumentException should be thrown while creating the " +
               "ViewMetadata instance");
      } catch (IllegalArgumentException iae) {
         // expected, it is working
      }
   }

   public void testBeforeAction() throws Exception {
      ValidViewHandler valid = new ValidViewHandler();
      ViewMetadata validMetadata = getViewMetadata(valid);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "non-existant");
      assertNull(valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, ValidViewHandler.SIMPLE_METHOD);
      assertSame(ValidViewHandler.SIMPLE_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "anotherName");
      assertSame(ValidViewHandler.ALIASED_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "a");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      valid.value = null;
      validMetadata.invokeBeforeAction(valid, "b");
      assertSame(ValidViewHandler.ARRAY_METHOD, valid.value);

      try {
         getViewMetadata(new InvalidBeforeActionViewHandler());
         fail("An IllegalArgumentException should be thrown while creating the " +
               "ViewMetadata instance");
      } catch (IllegalArgumentException iae) {
         // expected, it is working
      }
   }
}