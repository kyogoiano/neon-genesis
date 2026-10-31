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
package dev.neongenesis.tests.cdi;

import java.io.Serializable;

import io.helidon.http.Method;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientResponse;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;
import javax.naming.InitialContext;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.cdi.CommandExecutorCDI;
import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.tests.TestCase;

public class HelidonNimaCdiSmokeTest extends TestCase {

   public void testCdiLiteContainerAndHelidonNimaHealth() throws Exception {
      CommandExecutorCDI executor = CDI.current()
            .select(CommandExecutorCDI.class).get();
      assertNotNull(executor);

      TransactionalInjector injector = CDI.current()
            .select(TransactionalInjector.class).get();
      assertNotNull(injector);

      assertNotNull(getNimaServer());
      assertTrue(getNimaServer().isRunning());

      Http1Client client = Http1Client.builder().build();
      try (Http1ClientResponse response = client.method(Method.GET)
            .uri(getNimaServer().getHealthEndpointUrl())
            .request()) {
         assertEquals(200, response.status().code());
         assertEquals("UP", response.entity().as(String.class));
      }
   }

   public void testInProcessCdiLiteAndRemoteHelidonNimaExecution() throws Exception {
      String previousTransport = System.getProperty("genesis.aspect.cdiTransport");
      try {
         System.setProperty("genesis.aspect.cdiTransport", "local");
         SampleCdiCommand inProcessCmd = new SampleCdiCommand();
         assertEquals("echo:in-process:" + Status.STATUS_NO_TRANSACTION,
               inProcessCmd.echoRemotable("in-process"));
         assertEquals("tx:in-process:" + Status.STATUS_ACTIVE,
               inProcessCmd.echoTransactional("in-process"));
         assertEquals(0, inProcessCmd.invocations);

         System.setProperty("genesis.aspect.cdiTransport", "remote");
         SampleCdiCommand remoteNimaCmd = new SampleCdiCommand();
         assertEquals("echo:nima-http:" + Status.STATUS_NO_TRANSACTION,
               remoteNimaCmd.echoRemotable("nima-http"));
         assertEquals("tx:nima-http:" + Status.STATUS_ACTIVE,
               remoteNimaCmd.echoTransactional("nima-http"));
         assertEquals(0, remoteNimaCmd.invocations);
      } finally {
         if (previousTransport == null) {
            System.clearProperty("genesis.aspect.cdiTransport");
         } else {
            System.setProperty("genesis.aspect.cdiTransport", previousTransport);
         }
      }
   }

   public static class SampleCdiCommand implements Serializable {
      private static final long serialVersionUID = 1L;
      private int invocations;

      @Remotable
      public String echoRemotable(String input) throws Exception {
         invocations++;
         int status = ((UserTransaction)new InitialContext()
               .lookup("java:comp/UserTransaction")).getStatus();
         return "echo:" + input + ":" + status;
      }

      @Transactional
      public String echoTransactional(String input) throws Exception {
         invocations++;
         int status = ((UserTransaction)new InitialContext()
               .lookup("java:comp/UserTransaction")).getStatus();
         return "tx:" + input + ":" + status;
      }
   }
}
