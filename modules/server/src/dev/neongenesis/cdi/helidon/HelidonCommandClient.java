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
import java.lang.reflect.InvocationTargetException;

import io.helidon.http.HeaderNames;
import io.helidon.http.Method;
import io.helidon.webclient.http1.Http1Client;
import io.helidon.webclient.http1.Http1ClientResponse;

import dev.neongenesis.cdi.helidon.HelidonCommandServer.CommandRequest;
import dev.neongenesis.cdi.helidon.HelidonCommandServer.CommandResponse;
import dev.neongenesis.ejb.CommandExecutor;

public class HelidonCommandClient implements CommandExecutor {
   private final String endpointUrl;
   private final Http1Client httpClient;

   public HelidonCommandClient(String endpointUrl) {
      this.endpointUrl = endpointUrl;
      this.httpClient = Http1Client.builder().build();
   }

   public String getEndpointUrl() {
      return endpointUrl;
   }

   @Override
   public Object executeTransaction(Object target, String methodName,
         String[] classNames, Object[] args) throws ClassNotFoundException,
         IllegalAccessException, NoSuchMethodException,
         InvocationTargetException {
      return invokeRemote(true, target, methodName, classNames, args);
   }

   @Override
   public Object executeQuery(Object target, String methodName,
         String[] classNames, Object[] args) throws ClassNotFoundException,
         IllegalAccessException, NoSuchMethodException,
         InvocationTargetException {
      return invokeRemote(false, target, methodName, classNames, args);
   }

   private Object invokeRemote(boolean transactional, Object target,
         String methodName, String[] classNames, Object[] args)
         throws ClassNotFoundException, IllegalAccessException,
         NoSuchMethodException, InvocationTargetException {
      try {
         CommandRequest requestPayload = new CommandRequest(
               transactional, target, methodName, classNames, args);
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(requestPayload);
         }

         byte[] responseBytes;
         try (Http1ClientResponse response = httpClient.method(Method.POST)
               .uri(endpointUrl)
               .header(HeaderNames.CONTENT_TYPE, "application/octet-stream")
               .submit(baos.toByteArray());
              InputStream in = response.inputStream()) {
            responseBytes = in.readAllBytes();
         }

         CommandResponse commandResponse;
         try (ObjectInputStream ois = new ObjectInputStream(
               new ByteArrayInputStream(responseBytes))) {
            ois.setObjectInputFilter(info -> ObjectInputFilter.Status.ALLOWED);
            commandResponse = (CommandResponse)ois.readObject();
         }

         Throwable ex = commandResponse.getException();
         if (ex != null) {
            if (ex instanceof InvocationTargetException) {
               throw (InvocationTargetException)ex;
            }
            if (ex instanceof ClassNotFoundException) {
               throw (ClassNotFoundException)ex;
            }
            if (ex instanceof IllegalAccessException) {
               throw (IllegalAccessException)ex;
            }
            if (ex instanceof NoSuchMethodException) {
               throw (NoSuchMethodException)ex;
            }
            throw new InvocationTargetException(ex);
         }

         return commandResponse.getResult();
      } catch (InvocationTargetException | ClassNotFoundException
            | IllegalAccessException | NoSuchMethodException e) {
         throw e;
      } catch (Exception e) {
         throw new InvocationTargetException(e);
      }
   }
}
