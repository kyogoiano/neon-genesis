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

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;

public class ActionInvokerTest extends GenesisTestCase {
   private MockForm form;

   public ActionInvokerTest() {
      super("ActionInvoker Unit Test");
   }

   protected void setUp() throws Exception {
      form = new MockForm();
   }

   public void testInvoke() throws Exception {
      ActionInvoker.invoke(form, "action");

      // Assert that correct action was called
      assertEquals("action", form.getController().get(
            "invokeAction(actionName)"));
   }

   public void testRefresh() throws Exception {
      ActionInvoker.refresh(form);

      // Assert that controller.update() was called
      assertEquals(Boolean.TRUE, form.getController().get("update()"));
   }

   public void testSetupController() throws Exception {
      // Assert controller has not been setup
      assertTrue(!form.getController().isSetup());

      // 1st refresh
      ActionInvoker.refresh(form);
      // Assert that controller has been setup
      assertTrue(form.getController().isSetup());

      // 2nd refresh
      ActionInvoker.refresh(form);
      // Assert that controller has been setup
      assertTrue(form.getController().isSetup());
   }

}