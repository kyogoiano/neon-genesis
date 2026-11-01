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

import java.util.List;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.command.hibernate.AbstractHibernateCommand;
import dev.neongenesis.paging.Page;


public class DbActions extends AbstractHibernateCommand {

	@Transactional
	public void deleteAll() throws Exception {
        getSession().createMutationQuery("delete from HibernateBean").executeUpdate();
	}

	@Transactional
	public void insert(int number) throws Exception {
		HibernateBean bean;
		for (int i = 0; i < number; i++) {
			bean = new HibernateBean();
			bean.setCode(String.valueOf(i));
			getSession().persist(bean);
		}
	}

    @Remotable
    public List getPrimaryKeys() {
        return getSession().createQuery(
              "select bean.pk from HibernateBean bean", Long.class).list();
    }

    @Remotable
	public Page getPageUsingCriteria(int pageNumber, int resultsPerPage)
			throws Exception {
		return getPage(createCriteria(HibernateBean.class),
				pageNumber, resultsPerPage);
	}

    @Remotable
	public Page getPageUsingQuery(int pageNumber, int resultsPerPage)
			throws Exception {
		return getPage(getSession().createQuery("from HibernateBean",
              HibernateBean.class),
				pageNumber, resultsPerPage);
	}
}