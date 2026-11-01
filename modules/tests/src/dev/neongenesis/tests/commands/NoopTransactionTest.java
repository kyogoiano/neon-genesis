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

import javax.naming.InitialContext;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import java.io.Serializable;

import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.tests.TestCase;



public class NoopTransactionTest extends TestCase {

    public void testLocal() {
        NoopTransaction transaction = new NoopTransaction();
        transaction.localMethod();
        assertTrue(transaction.value == 1);
    }

    public void testTransactional() throws Exception {
        NoopTransaction transaction = new NoopTransaction();
        assertEquals(transaction.invokeTransactional(), Status.STATUS_ACTIVE);
        assertTrue(transaction.value == 0);
    }

    public static class NoopTransaction implements Serializable {
        private static final long serialVersionUID = 1L;

        private int value;

        @Transactional
        public int invokeTransactional() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup(
                  "java:comp/UserTransaction")).getStatus();
        }

        public void localMethod() {
            value++;
        }
    }

}