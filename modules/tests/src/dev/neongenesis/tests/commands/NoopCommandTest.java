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
package dev.neongenesis.tests.commands;

import java.io.Serializable;

import javax.naming.InitialContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.tests.TestCase;


public class NoopCommandTest extends TestCase {
    public void testLocal() {
        NoopCommand command = new NoopCommand();
        command.local();
        assertTrue(command.value == 1);
    }

    public void testRemotable() throws Exception {
        NoopCommand command = new NoopCommand();
        assertEquals(command.remotable(), Status.STATUS_NO_TRANSACTION);
        assertTrue(command.value == 0);
    }

    public void testTransactional() throws Exception {
        NoopCommand command = new NoopCommand();
        assertEquals(command.transactional(), Status.STATUS_ACTIVE);
        assertTrue(command.value == 0);
    }

    public static class NoopCommand implements Serializable {

        private int value;

        @Remotable
        public int remotable() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup(
                  "java:comp/UserTransaction")).getStatus();
        }

        public void local() {
            value++;
        }

        @Transactional
        public int transactional() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup(
                  "java:comp/UserTransaction")).getStatus();
        }
    }
}