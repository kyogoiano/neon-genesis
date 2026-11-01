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

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.command.hibernate.AbstractHibernateCommand;
import dev.neongenesis.tests.TestCase;
import org.hibernate.FlushMode;
import org.hibernate.Session;


public class LocalCommandExecutionTest extends TestCase {

    public void testLocalCommand() throws Exception {
        NoopCommand command = new NoopCommand();
        command.local();
        assertTrue(command.value == 1);
        assertNull(command.hibernateSession);
    }

    public void testRemotableCommand() {
        NoopCommand command = new NoopCommand();
        command.remotable();
        assertTrue(command.value == 1);
        assertEquals(FlushMode.AUTO, command.flushMode);
    }

    public void testTransactionalCommand() throws Exception {
        NoopCommand command = new NoopCommand();
        command.transactional();
        assertTrue(command.value == 1);
        assertEquals(FlushMode.COMMIT, command.flushMode);
    }

    public void testNestedCommand() throws Exception {
       NoopCommand command = new NoopCommand();
       assertTrue("Sessions should be the same in nested command invocations", 
             command.nested());
    }

    public void testLocalQuery() {
        NoopQuery query = new NoopQuery();
        query.localMethod();
        assertTrue(query.value == 1);
        assertNull(query.hibernateSession);
    }

    public void testRemotableQuery() throws Exception {
        NoopQuery query = new NoopQuery();
        query.invokeRemotable();
        assertTrue(query.value == 1);
        assertEquals(FlushMode.AUTO, query.flushMode);
    }

    public void testLocalTransaction() {
        NoopTransaction transaction = new NoopTransaction();
        transaction.localMethod();
        assertTrue(transaction.value == 1);
        assertNull(transaction.hibernateSession);
    }

    public void testTransactionalTransaction() {
        NoopTransaction transaction = new NoopTransaction();
        transaction.invokeTransactional();
        assertTrue(transaction.value == 1);
        assertEquals(FlushMode.COMMIT, transaction.flushMode);
    }

    public static class NoopCommand extends AbstractHibernateCommand {

        private FlushMode flushMode;
        private Session hibernateSession;
        private int value;

        @Remotable
        public boolean nested() {
           return getSession() == remotable();
        }

        @Remotable
        public Session remotable() {
            value++;
            flushMode = getSession().getHibernateFlushMode();
            
            return getSession();
        }

        public void local() {
            value++;
            hibernateSession = getSession();
        }

        @Transactional
        public void transactional() {
            value++;
            flushMode = getSession().getHibernateFlushMode();
        }
    }

    public static class NoopQuery extends AbstractHibernateCommand {

        private FlushMode flushMode;
        private Session hibernateSession;
        private int value;

        @Remotable
        public void invokeRemotable() {
            value++;
            flushMode = getSession().getHibernateFlushMode();
        }

        public void localMethod() {
            value++;
            hibernateSession = getSession();
        }
    }

    public static class NoopTransaction extends AbstractHibernateCommand {

        private FlushMode flushMode;
        private Session hibernateSession;
        private int value;

        @Transactional
        public void invokeTransactional() {
            value++;
            flushMode = getSession().getHibernateFlushMode();
        }

        public void localMethod() {
            value++;
            hibernateSession = getSession();
        }
    }

}