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
package dev.neongenesis.tests;

import java.util.Map;
import javax.naming.Context;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import dev.neongenesis.cdi.CDITransactionContext;
import dev.neongenesis.cdi.CommandExecutorCDI;
import dev.neongenesis.cdi.helidon.HelidonCommandServer;
import dev.neongenesis.cdi.hibernate.CDIHibernateTransactionalInjector;
import dev.neongenesis.commons.beanutils.ConverterRegistry;
import dev.neongenesis.ejb.lite.WebProfileEjbLiteContainer;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.FormMetadataFactory;
import dev.neongenesis.ui.metadata.ViewMetadata;
import dev.neongenesis.ui.metadata.ViewMetadataFactory;
import dev.neongenesis.util.GenesisUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.jxpath.JXPathContextFactory;


public class TestCase extends junit.framework.TestCase {
   private static final SeContainer CDI_CONTAINER;
   private static final HelidonCommandServer NIMA_SERVER;
   private static final WebProfileEjbLiteContainer EJB_LITE_CONTAINER;

   static {
      BeanUtilsBean.setInstance(new BeanUtilsBean(new ConverterRegistry(),
            new PropertyUtilsBean()));
      System.setProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY,
            "dev.neongenesis.commons.jxpath.JXPathContextFactory");
      System.setProperty(Context.INITIAL_CONTEXT_FACTORY,
            "dev.neongenesis.cdi.jndi.CDIInitialContextFactory");

      CDI_CONTAINER = SeContainerInitializer.newInstance()
            .disableDiscovery()
            .addBeanClasses(
                  CommandExecutorCDI.class,
                  CDIHibernateTransactionalInjector.class,
                  CDITransactionContext.class)
            .initialize();

      String profile = System.getProperty("genesis.test.profile", "core");
      if ("web".equalsIgnoreCase(profile)) {
         System.setProperty("genesis.aspect.executionMode", "ejb");
         NIMA_SERVER = null;
         EJB_LITE_CONTAINER = new WebProfileEjbLiteContainer();
      } else {
         System.setProperty("genesis.aspect.executionMode", "cdi");
         EJB_LITE_CONTAINER = null;
         NIMA_SERVER = new HelidonCommandServer(0);
         System.setProperty("genesis.aspect.nima.url",
               NIMA_SERVER.getCommandEndpointUrl());
         System.setProperty("genesis.aspect.nima.healthUrl",
               NIMA_SERVER.getHealthEndpointUrl());
      }

      Runtime.getRuntime().addShutdownHook(Thread.ofVirtual()
            .name("Neon Genesis test shutdown")
            .unstarted(() -> {
               if (NIMA_SERVER != null) {
                  NIMA_SERVER.stop();
               }
               if (EJB_LITE_CONTAINER != null) {
                  EJB_LITE_CONTAINER.close();
               }
            }));
   }

   public static HelidonCommandServer getNimaServer() {
      return NIMA_SERVER;
   }

   public static WebProfileEjbLiteContainer getEjbLiteContainer() {
      return EJB_LITE_CONTAINER;
   }

	protected FormMetadata getFormMetadata(final Object form) {
		return ((FormMetadataFactory) form).getFormMetadata(form.getClass());
	}

	protected ViewMetadata getViewMetadata(final Object viewHandler) {
		return ((ViewMetadataFactory) viewHandler).getViewMetadata(viewHandler.getClass());
	}

	public static void assertDescribedMapEquals(Map map, Map map2) {
		GenesisUtils.normalizeMap(map);
		GenesisUtils.normalizeMap(map2);
		junit.framework.TestCase.assertEquals(map, map2);
	}
}