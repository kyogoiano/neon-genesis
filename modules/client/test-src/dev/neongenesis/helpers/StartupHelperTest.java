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
package dev.neongenesis.helpers;

import java.util.Locale;
import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.commons.beanutils.converters.BigDecimalConverter;
import dev.neongenesis.commons.beanutils.converters.BooleanConverter;
import dev.neongenesis.text.BooleanFormatter;
import dev.neongenesis.text.DefaultFormatter;
import dev.neongenesis.text.FormatterRegistry;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.jxpath.JXPathContextFactory;

public class StartupHelperTest extends GenesisTestCase {
   private StartupHelper startupHelper;

   protected void tearDown() {
      startupHelper = null;
   }
   
   public void testNoArgConstructor() {
      startupHelper = new StartupHelper();

      //validating with the default values.
      verifyBeforeInitialize(Locale.getDefault(), "MM/dd/yyyy");
   }
   
   public void testTwoArgConstructor() {
      //change the locale and dateFormater
      startupHelper = new StartupHelper(Locale.JAPANESE,"dd-MM-yyyy");
      //validating with the changed value.
      verifyBeforeInitialize(Locale.JAPANESE,"dd-MM-yyyy");
   }
   
   private void verifyBeforeInitialize(Locale locale, String dateFormat) {
      //testing if the locale isn't null.
      assertNotNull(startupHelper.getLocale());
      //testing if the locale is the same.
      assertEquals(locale, startupHelper.getLocale());
      //testing if the DateFormater isn't null.
      assertNotNull(startupHelper.getDateFormat());
      //testing if the date formater is the same.
      assertEquals(dateFormat,startupHelper.getDateFormat());
      //testing if the converters collection contains one element.
      assertEquals(1, startupHelper.getConverters().size());
      //testing if the formatters collection contains one element.
      assertEquals(1, startupHelper.getFormatters().size());
      //testing if the JXPathContextFactoryClassName is still the default value.
      assertEquals("dev.neongenesis.commons.jxpath.JXPathContextFactory",
            startupHelper.getJXPathContextFactoryClassName());
      //testing if isLoadValidatorRules is still the default value.
      assertTrue(startupHelper.isLoadValidatorRules());
      //testing if isRunNoopCommand is still the default value.
      assertTrue(startupHelper.isRunNoopCommand());
   }
   
   public void testInitializeWithNoChanges() {
      startupHelper = new StartupHelper();

      //test with the default values.
      verifyBeforeInitialize(Locale.getDefault(), "MM/dd/yyyy");
      
      //initializing
      startupHelper.initialize();
      
      //verifying the values
      assertNotNull(startupHelper.getBeanUtilsBean());
      //verifying the property value of FACTORY_NAME_PROPERTY
      assertNotNull(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY));
      assertEquals(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY),
            "dev.neongenesis.commons.jxpath.JXPathContextFactory");
      
   }
   public void testInitializeWithChanges() {
      startupHelper = new StartupHelper();

      //test with the default values.
      verifyBeforeInitialize(Locale.getDefault(), "MM/dd/yyyy");
      //changing some properties besides add some converters and formatters.

      //setting the new BeanUtilsBean.
      BeanUtilsBean bean = new BeanUtilsBean(new TestConvertUtilsBean(), 
            new PropertyUtilsBean());
      startupHelper.setBeanUtilsBean(bean);

      //setting the new Locale
      startupHelper.setLocale(Locale.ENGLISH);
      //setting the new DateFormat
      startupHelper.setDateFormat("MM-dd-yyyy");
      //setting the new JXPathContextFactoryClassName
      startupHelper.setJXPathContextFactoryClassName("dev.neongenesis.helpers.TestJXPathContextFactory");

      //adding some converters
      BigDecimalConverter bigDecimalConverter = new BigDecimalConverter();
      startupHelper.addConverter(BigDecimalConverter.class, bigDecimalConverter);
      BooleanConverter booleanConverter = new BooleanConverter();
      startupHelper.addConverter(BooleanConverter.class, booleanConverter);

      //adding some formatters
      BooleanFormatter booleanFormatter = new BooleanFormatter();
      startupHelper.addFormatter(BooleanFormatter.class, booleanFormatter);
      DefaultFormatter defaultFormatter = new DefaultFormatter();
      startupHelper.addFormatter(DefaultFormatter.class, defaultFormatter);
      
      //initializing
      startupHelper.initialize();
      
      //verifying the values
      assertNotNull(startupHelper.getBeanUtilsBean());
      //validating if the BeanUtilsBean is the same instance as the changed
      assertSame(bean, BeanUtilsBean.getInstance());

      //verifying the property value of FACTORY_NAME_PROPERTY
      assertNotNull(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY));
      assertEquals(System.getProperties().getProperty(JXPathContextFactory.FACTORY_NAME_PROPERTY),
            "dev.neongenesis.helpers.TestJXPathContextFactory");

      //testing the converters
      assertNotNull(startupHelper.getConverter(BigDecimalConverter.class));
      assertSame(bigDecimalConverter, startupHelper.getConverter(
            BigDecimalConverter.class));
      assertNotNull(ConvertUtils.lookup(BigDecimalConverter.class));
      assertSame(bigDecimalConverter, ConvertUtils.lookup(
            BigDecimalConverter.class));
      
      assertNotNull(startupHelper.getConverter(BooleanConverter.class));
      assertSame(booleanConverter, startupHelper.getConverter(
            BooleanConverter.class));
      assertNotNull(ConvertUtils.lookup(BooleanConverter.class));
      assertSame(startupHelper.getConverter(BooleanConverter.class),
            ConvertUtils.lookup(BooleanConverter.class));
      
      //testing the formatters
      assertNotNull(startupHelper.getFormatter(BooleanFormatter.class));
      assertSame(booleanFormatter, startupHelper.getFormatter(
            BooleanFormatter.class));
      assertNotNull(FormatterRegistry.getInstance().get(BooleanFormatter.class));
      assertEquals(startupHelper.getFormatter(BooleanFormatter.class),
            FormatterRegistry.getInstance().get(BooleanFormatter.class));
      
      assertNotNull(startupHelper.getFormatter(DefaultFormatter.class));
      assertSame(defaultFormatter, startupHelper.getFormatter(DefaultFormatter.class));
      assertNotNull(FormatterRegistry.getInstance().get(BooleanFormatter.class));
      assertEquals(startupHelper.getFormatter(DefaultFormatter.class),
            FormatterRegistry.getInstance().get(DefaultFormatter.class));

      //testing the map collections
      assertEquals(3, startupHelper.getConverters().size());
      assertEquals(3, startupHelper.getFormatters().size());
   }

   public void testRemoveConverter() {
      startupHelper = new StartupHelper();

      BooleanConverter booleanConverter = new BooleanConverter();
      startupHelper.addConverter(BooleanConverter.class, booleanConverter);

      assertSame(booleanConverter, startupHelper.getConverter(
            BooleanConverter.class));

      int size = startupHelper.getConverters().size();

      assertSame(booleanConverter, startupHelper.removeConverter(
            BooleanConverter.class));
      assertEquals(size - 1, startupHelper.getConverters().size());
   }

   public void testRemoveFormatter() {
      startupHelper = new StartupHelper();

      BooleanFormatter booleanFormatter = new BooleanFormatter();
      startupHelper.addFormatter(BooleanFormatter.class, booleanFormatter);

      assertSame(booleanFormatter, startupHelper.getFormatter(
            BooleanFormatter.class));

      int size = startupHelper.getFormatters().size();

      assertSame(booleanFormatter, startupHelper.removeFormatter(
            BooleanFormatter.class));
      assertEquals(size - 1, startupHelper.getFormatters().size());
   }
}

class TestConvertUtilsBean extends ConvertUtilsBean {
}

class TestJXPathContextFactory extends org.apache.commons.jxpath.ri.JXPathContextFactoryReferenceImpl {
}