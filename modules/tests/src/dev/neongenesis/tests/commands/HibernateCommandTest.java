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

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.command.hibernate.AbstractHibernateCommand;
import dev.neongenesis.command.hibernate.AbstractHibernateQuery;
import dev.neongenesis.command.hibernate.AbstractHibernateTransaction;
import dev.neongenesis.tests.TestCase;



public class HibernateCommandTest extends TestCase {
    public void testCommandLocal() {
       System.out.println("local");
        HibernateCommand command = new HibernateCommand();
        assertFalse(command.local());
        assertTrue(command.value == 1);
    }

    public void testCommandRemotable() throws Exception {
       System.out.println("remotable");
        HibernateCommand command = new HibernateCommand();
        assertEquals(command.remotable(), Status.STATUS_NO_TRANSACTION);
        assertTrue(command.value == 0);
    }

    public void testCommandTransactional() throws Exception {
        HibernateCommand command = new HibernateCommand();
        assertEquals(command.transactional(), Status.STATUS_ACTIVE);
        assertTrue(command.value == 0);
    }

    public void testQueryLocal() {
        HibernateQuery command = new HibernateQuery();
        assertFalse(command.local());
        assertTrue(command.value == 1);
    }

    public void testQueryRemotable() throws Exception {
        HibernateQuery command = new HibernateQuery();
        assertEquals(command.invokeRemotable(), Status.STATUS_NO_TRANSACTION);
        assertTrue(command.value == 0);
    }

    public void testTransactionLocal() {
        HibernateTransaction command = new HibernateTransaction();
        assertFalse(command.local());
        assertTrue(command.value == 1);
    }

    public void testTransactionTransactional() throws Exception {
        HibernateTransaction command = new HibernateTransaction();
        assertEquals(command.invokeTransactional(), Status.STATUS_ACTIVE);
        assertTrue(command.value == 0);
    }

    public static class HibernateCommand extends AbstractHibernateCommand {

        private int value;

        public boolean local() {
            value++;
            return getSession() != null;
        }

        @Remotable
        public int remotable() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup(
                  "java:comp/UserTransaction")).getStatus();
        }

        @Transactional
        public int transactional() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup(
                  "java:comp/UserTransaction")).getStatus();
        }

    }

    public static class HibernateQuery extends AbstractHibernateQuery {

        private int value;

        public boolean local() {
            value++;
            return getSession() != null;
        }

        public int invokeRemotable() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup(
                  "java:comp/UserTransaction")).getStatus();
        }
    }

    public static class HibernateTransaction extends AbstractHibernateTransaction {

        private int value;

        public boolean local() {
            value++;
            return getSession() != null;
        }

        public int invokeTransactional() throws Exception {
            value++;
            return ((UserTransaction) new InitialContext().lookup(
                  "java:comp/UserTransaction")).getStatus();
        }
    }

}