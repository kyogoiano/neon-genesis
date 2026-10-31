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

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.tests.TestCase;
import org.hibernate.HibernateException;

public class AbstractHibernateCommandTest extends TestCase {
   private final DbActions actions = new DbActions();

   public void tearDown() throws Exception {
      actions.deleteAll();
   }

   public void testAppendClause() throws Exception {
	actions.insert(2);

	AbstractHibernateCommand command = new AbstractHibernateCommand();
	assertNotNull(command.executeQuery(actions.getPrimaryKeys()));
   }

   public static class AbstractHibernateCommand extends 
	   dev.neongenesis.command.hibernate.AbstractHibernateCommand 
	   implements Serializable {

    @Remotable
	public HibernateBean executeQuery(List pks) throws HibernateException {
	   StringBuffer where = new StringBuffer();
	   Map parameters = new HashMap();

	   appendClause(where, "h.pk in (:pks)");
	   parameters.put("pks", pks);

	   appendClause(where, "h.pk = :pk");
	   parameters.put("pk", pks.get(0));

	   return (HibernateBean) generateQuery("select h", 
		   Collections.singletonMap("h", "HibernateBean"), where.toString(), 
		   "", parameters).uniqueResult();
	}
   }
}