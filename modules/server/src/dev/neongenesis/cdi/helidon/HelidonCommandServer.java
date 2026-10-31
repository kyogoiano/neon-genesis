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
package dev.neongenesis.cdi.helidon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import io.helidon.http.HeaderNames;
import io.helidon.http.Status;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.http.ServerRequest;
import io.helidon.webserver.http.ServerResponse;

import jakarta.enterprise.inject.spi.CDI;

import dev.neongenesis.cdi.CommandExecutorCDI;
import dev.neongenesis.ejb.CommandExecutor;

public class HelidonCommandServer {
   public static final String COMMAND_PATH = "/genesis/command";
   public static final String HEALTH_PATH = "/genesis/health";

   private final WebServer webServer;

   public HelidonCommandServer(int port) {
      this.webServer = WebServer.builder()
            .host("127.0.0.1")
            .port(port)
            .routing(this::configureRouting)
            .build()
            .start();
   }

   private void configureRouting(HttpRouting.Builder routing) {
      routing.get(HEALTH_PATH, (req, res) -> {
         res.status(Status.OK_200).send("UP");
      });
      routing.post(COMMAND_PATH, this::handleCommand);
   }

   private void handleCommand(ServerRequest req, ServerResponse res) {
      CommandResponse commandResponse;
      try {
         byte[] requestBytes;
         try (InputStream in = req.content().inputStream()) {
            requestBytes = in.readAllBytes();
         }

         CommandRequest commandRequest;
         try (ObjectInputStream ois = new ObjectInputStream(
               new ByteArrayInputStream(requestBytes))) {
            ois.setObjectInputFilter(info -> ObjectInputFilter.Status.ALLOWED);
            commandRequest = (CommandRequest)ois.readObject();
         }

         CommandExecutor executor = resolveCommandExecutor();
         Object result;
         if (commandRequest.isTransactional()) {
            result = executor.executeTransaction(commandRequest.getTarget(),
                  commandRequest.getMethodName(), commandRequest.getClassNames(),
                  commandRequest.getArgs());
         } else {
            result = executor.executeQuery(commandRequest.getTarget(),
                  commandRequest.getMethodName(), commandRequest.getClassNames(),
                  commandRequest.getArgs());
         }
         commandResponse = new CommandResponse(result, null);
      } catch (InvocationTargetException ite) {
         commandResponse = new CommandResponse(null, ite);
      } catch (Throwable t) {
         commandResponse = new CommandResponse(null,
               new InvocationTargetException(t));
      }

      try {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(commandResponse);
         }
         res.header(HeaderNames.CONTENT_TYPE, "application/octet-stream");
         res.status(Status.OK_200).send(baos.toByteArray());
      } catch (Exception serializeEx) {
         res.status(Status.INTERNAL_SERVER_ERROR_500)
               .send(serializeEx.getMessage());
      }
   }

   protected CommandExecutor resolveCommandExecutor() {
      return CDI.current().select(CommandExecutorCDI.class).get();
   }

   public int getPort() {
      return webServer.port();
   }

   public String getCommandEndpointUrl() {
      return "http://127.0.0.1:" + getPort() + COMMAND_PATH;
   }

   public String getHealthEndpointUrl() {
      return "http://127.0.0.1:" + getPort() + HEALTH_PATH;
   }

   public boolean isRunning() {
      return webServer.isRunning();
   }

   public void stop() {
      if (webServer.isRunning()) {
         webServer.stop();
      }
   }

   public static final class CommandRequest implements Serializable {
      private static final long serialVersionUID = 1L;

      private final boolean transactional;
      private final Object target;
      private final String methodName;
      private final String[] classNames;
      private final Object[] args;

      public CommandRequest(boolean transactional, Object target,
            String methodName, String[] classNames, Object[] args) {
         this.transactional = transactional;
         this.target = target;
         this.methodName = methodName;
         this.classNames = classNames;
         this.args = args;
      }

      public boolean isTransactional() {
         return transactional;
      }

      public Object getTarget() {
         return target;
      }

      public String getMethodName() {
         return methodName;
      }

      public String[] getClassNames() {
         return classNames;
      }

      public Object[] getArgs() {
         return args;
      }
   }

   public static final class CommandResponse implements Serializable {
      private static final long serialVersionUID = 1L;

      private final Object result;
      private final Throwable exception;

      public CommandResponse(Object result, Throwable exception) {
         this.result = result;
         this.exception = exception;
      }

      public Object getResult() {
         return result;
      }

      public Throwable getException() {
         return exception;
      }
   }
}
