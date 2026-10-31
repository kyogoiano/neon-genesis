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

import java.util.Map;
import dev.neongenesis.command.hibernate.CriteriaResolver;
import dev.neongenesis.command.hibernate.HibernateCriteria;
import org.hibernate.Criteria;

public class MockHibernateCriteria implements HibernateCriteria, 
         CriteriaResolver {
   private Map map;
   private Criteria criteria;

   public Map getPropertiesMap() {
      return map;
   }

   public void setPropertiesMap(Map map) {
      this.map = map;
   }

   protected Criteria getCriteria() {
      return criteria;
   }

   public void setCriteria(Criteria criteria) {
      this.criteria = criteria;
   }
}