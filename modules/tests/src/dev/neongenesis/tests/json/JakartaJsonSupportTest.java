/*
 * Neon Genesis
 * Copyright (C) 2026 Leandro Cardoso de Andrade (@kyogoiano <leandro.ueg@gmail.com>)
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
package dev.neongenesis.tests.json;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParsingException;

import dev.neongenesis.tests.TestCase;

public class JakartaJsonSupportTest extends TestCase {
   public void testJsonBindingRoundTrip() throws Exception {
      try (Jsonb jsonb = JsonbBuilder.create()) {
         String encoded = jsonb.toJson(new SampleValue("Neon", 25));
         SampleValue decoded = jsonb.fromJson(encoded, SampleValue.class);

         assertEquals("Neon", decoded.getName());
         assertEquals(25, decoded.getVersion());
      }
   }

   public void testJsonProcessingReadsObject() {
      try (JsonReader reader = Json.createReader(
            new StringReader("{\"name\":\"Neon\",\"version\":25}"))) {
         JsonObject object = reader.readObject();

         assertEquals("Neon", object.getString("name"));
         assertEquals(25, object.getInt("version"));
      }
   }

   public void testJsonProcessingReportsMalformedInput() {
      try (JsonReader reader = Json.createReader(new StringReader("{"))) {
         reader.readValue();
         fail("Malformed JSON should not be accepted");
      } catch (JsonParsingException expected) {
         assertNotNull(expected.getMessage());
      }
   }

   public void testJsonBindingReportsMalformedInput() throws Exception {
      try (Jsonb jsonb = JsonbBuilder.create()) {
         jsonb.fromJson("{", SampleValue.class);
         fail("Malformed JSON should not be accepted");
      } catch (JsonbException expected) {
         assertNotNull(expected.getMessage());
      }
   }

   public static class SampleValue {
      private String name;
      private int version;

      public SampleValue() {
      }

      public SampleValue(String name, int version) {
         this.name = name;
         this.version = version;
      }

      public String getName() {
         return name;
      }

      public void setName(String name) {
         this.name = name;
      }

      public int getVersion() {
         return version;
      }

      public void setVersion(int version) {
         this.version = version;
      }
   }
}
