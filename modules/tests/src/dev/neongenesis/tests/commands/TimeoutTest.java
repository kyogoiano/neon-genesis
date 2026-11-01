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

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.exception.TimeoutException;
import dev.neongenesis.tests.TestCase;



public class TimeoutTest extends TestCase {

    public void testDontWait() throws Exception {
       System.out.println("dont wait");
        TimeoutCommand command = new TimeoutCommand();
        Exception e = null;
        try{
            command.dontWait();
        }
        catch(Exception ex){
            e = ex;
        }
        assertNull(e);
    }

    public void testUsesVirtualThread() {
        assertTrue(new TimeoutCommand().runsOnVirtualThread());
    }

    public void testWaitALot() throws Exception {
        TimeoutCommand command = new TimeoutCommand();
        Exception e = null;

        try{
            command.takesALot();
        }
        catch(Exception ex){
            e = ex;
        }

        assertNotNull(e);
        assertTrue(e instanceof TimeoutException);
    }

    public static class TimeoutCommand implements Serializable {

        @Remotable
        public void takesALot() throws InterruptedException {
            System.out.println("takes a loooong time");
            Thread.sleep(20000);
        }

        @Remotable
        public void dontWait() {
           System.out.println("dont wait");   
        }

        @Remotable
        public boolean runsOnVirtualThread() {
            return Thread.currentThread().isVirtual();
        }

    }

}