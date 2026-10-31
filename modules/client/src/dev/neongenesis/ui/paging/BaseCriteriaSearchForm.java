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
package dev.neongenesis.ui.paging;

import java.util.Map;
import dev.neongenesis.helpers.CriteriaPropertyHelper;
import dev.neongenesis.paging.Page;
import dev.neongenesis.command.hibernate.CriteriaResolver;
import dev.neongenesis.command.hibernate.HibernateCriteria;

public abstract class BaseCriteriaSearchForm extends BaseSearchForm {
   private Map values;

   protected abstract HibernateCriteria getHibernateCriteria();

   protected Page performSearch() throws Exception {
      final HibernateCriteria criteria = getHibernateCriteria();

      if (isResetSearch() || values == null) {
         CriteriaPropertyHelper.fillCriteria(criteria, this);
         values = ((CriteriaResolver)criteria).getPropertiesMap();
      } else {
         ((CriteriaResolver)criteria).setPropertiesMap(values);
      }

      return performSearch(getPageNumber());
   }

   protected abstract Page performSearch(int pageNumber) throws Exception;
}