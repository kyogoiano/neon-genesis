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
package dev.neongenesis.tests.embedded;

import javax.naming.InitialContext;
import dev.neongenesis.ejb.CommandExecutor;
import dev.neongenesis.ejb.CommandExecutorLocal;
import dev.neongenesis.tests.TestCase;

public class EmbeddedContainerSmokeTest extends TestCase {
   public void testPublishesEjbLiteLocalBusinessView() throws Exception {
      CommandExecutorLocal localExecutor = (CommandExecutorLocal)new InitialContext().lookup(
            "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal");
      assertNotNull(localExecutor);

      CommandExecutor executor = (CommandExecutor)new InitialContext().lookup(
            "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutor");
      assertNotNull(executor);
   }
}
