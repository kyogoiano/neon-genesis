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
package dev.neongenesis.tests.webprofile;

import jakarta.enterprise.inject.spi.CDI;
import javax.naming.InitialContext;

import dev.neongenesis.cdi.CommandExecutorCDI;
import dev.neongenesis.cdi.hibernate.CDIHibernateTransactionalInjector;
import dev.neongenesis.command.TransactionalInjector;
import dev.neongenesis.ejb.CommandExecutorEJB;
import dev.neongenesis.ejb.CommandExecutorLocal;
import dev.neongenesis.tests.TestCase;
import dev.neongenesis.tests.hibernate.EJBHibernateTransactionalInjectorTest.HibernateCommand;
import dev.neongenesis.tests.hibernate.HibernateBean;

public class WebProfileCdiEjbTest extends TestCase {
   public void tearDown() throws Exception {
      new HibernateCommand().deleteAll();
   }

   public void testCdiBeansActiveAlongsideEjbLiteBeans() throws Exception {
      // Verify EJB Lite 4.0 @Local business interface is bound in JNDI
      CommandExecutorLocal ejbLocal = (CommandExecutorLocal)new InitialContext().lookup(
            "java:global/container/CommandExecutor!dev.neongenesis.ejb.CommandExecutorLocal");
      assertNotNull(ejbLocal);

      // Verify CDI 4.0 beans are active in the same Web Profile runtime
      CommandExecutorCDI cdiExecutor = CDI.current().select(CommandExecutorCDI.class).get();
      assertNotNull(cdiExecutor);
      TransactionalInjector cdiInjector = CDI.current().select(TransactionalInjector.class).get();
      assertTrue(cdiInjector instanceof CDIHibernateTransactionalInjector);

      // Verify CommandExecutorEJB receives CDI-injected TransactionalInjector when JNDI env-entry is absent
      assertNotNull(getEjbLiteContainer());
      CommandExecutorEJB managedEjb = getEjbLiteContainer().newManagedEjbInstance();
      assertNotNull(managedEjb.getTransactionalInjector());
      assertTrue(managedEjb.getTransactionalInjector() instanceof CDIHibernateTransactionalInjector);

      // Execute a @Transactional and @Remotable Hibernate command through EJB Lite + CDI
      HibernateCommand command = new HibernateCommand();
      command.insertBean("webProfileBean");
      HibernateBean persisted = command.getBean("webProfileBean");
      assertNotNull(persisted);
      assertEquals("webProfileBean", persisted.getCode());
   }
}
