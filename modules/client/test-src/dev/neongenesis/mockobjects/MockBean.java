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
package dev.neongenesis.mockobjects;

import java.io.Serializable;

public class MockBean implements Serializable {
   private String key;
   private String value;
   private MockBean bean;

   public MockBean(String key, String value) {
      this(key, value, null);
   }

   public MockBean(String key, String value, MockBean bean) {
      this.key = key;
      this.value = value;
      this.bean = bean;
   }

   public String getKey() {
      return key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public MockBean getBean() {
      return bean;
   }

   public void setBean(MockBean bean) {
      this.bean = bean;
   }

   public String toString() {
      return key;
   }
}
