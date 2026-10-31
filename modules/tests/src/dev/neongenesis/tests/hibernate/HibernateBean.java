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
package dev.neongenesis.tests.hibernate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;


@Table(name = "HibernateBean")
@Entity
public class HibernateBean implements Serializable {

   private Long pk;
   private String code;

   @Id
   @GeneratedValue(strategy= GenerationType.AUTO)
   public Long getPk() {
      return pk;
   }

   public void setPk(Long pk) {
      this.pk = pk;
   }

   public String getCode() {
      return code;
   }

   public void setCode(String codigo) {
      this.code = codigo;
   }
   
   public String toString(){
      return getClass().getName() + " - pk: " + getPk() + ", code: " + getCode();
   }
}