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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import dev.neongenesis.command.NoopCommand;
import dev.neongenesis.commons.beanutils.ConverterRegistry;
import dev.neongenesis.text.FormatAdapter;
import dev.neongenesis.text.Formatter;
import dev.neongenesis.text.FormatterRegistry;
import dev.neongenesis.ui.ValidationUtils;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StartupHelper {
   private static Log log = LogFactory.getLog(StartupHelper.class);
   private BeanUtilsBean beanUtilsBean = new BeanUtilsBean(
         new ConverterRegistry(), new PropertyUtilsBean());
   private String jxpathContextFactoryKey = "org.apache.commons.jxpath.JXPathContextFactory"; // NOI18N
   private String jxpathContextFactoryClassName =
         "dev.neongenesis.commons.jxpath.JXPathContextFactory"; // NOI18N
   private final Map converters = new HashMap();
   private final Map formatters = new HashMap();
   private Locale locale;
   private String dateFormat;
   private boolean runNoopCommand = true;
   private boolean loadValidatorRules = true;

   public StartupHelper() {
      this(Locale.getDefault(), "MM/dd/yyyy"); // NOI18N
   }

   public StartupHelper(final Locale locale, final String dateFormat) {
      setLocale(locale);
      setDateFormat(dateFormat);
   }

   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   public Locale getLocale() {
      return locale;
   }

   public void setDateFormat(String dateFormat) {
      this.dateFormat = dateFormat;

      addConverter(Date.class, new DateLocaleConverter(null, locale, dateFormat));
      addFormatter(Date.class, new FormatAdapter(new SimpleDateFormat(dateFormat),
            true));
   }

   public String getDateFormat() {
      return dateFormat;
   }

   public void setBeanUtilsBean(BeanUtilsBean beanUtilsBean) {
      this.beanUtilsBean = beanUtilsBean;
   }

   public BeanUtilsBean getBeanUtilsBean() {
      return beanUtilsBean;
   }

   protected void registerBeanUtilsBean() {
      log.info("Setting BeanUtilsBean instance"); // NOI18N
      BeanUtilsBean.setInstance(getBeanUtilsBean());
   }

   public String getJXPathContextFactoryClassName() {
      return jxpathContextFactoryClassName;
   }

   public void setJXPathContextFactoryClassName(String jxpathContextFactoryClassName) {
      this.jxpathContextFactoryClassName = jxpathContextFactoryClassName;
   }

   protected void registerJXPathContextFactory() {
      log.info("Registering JXPathContextFactory class name"); // NOI18N
      System.setProperty(jxpathContextFactoryKey, jxpathContextFactoryClassName);
   }

   public Converter addConverter(Class clazz, Converter converter) {
      return (Converter)converters.put(clazz, converter);
   }

   public Converter removeConverter(Class clazz) {
      return (Converter)converters.remove(clazz);
   }

   public Converter getConverter(Class clazz) {
      return (Converter)converters.get(clazz);
   }

   public Map getConverters() {
      return new HashMap(converters);
   }

   protected void registerConverters() {
      log.info("Registering converters"); // NOI18N

      for (final Iterator i = converters.entrySet().iterator(); i.hasNext(); ) {
         Map.Entry entry = (Map.Entry)i.next();

         ConvertUtils.register((Converter)entry.getValue(), 
               (Class)entry.getKey());
      }
   }

   public Formatter addFormatter(Class clazz, Formatter formatter) {
      return (Formatter)formatters.put(clazz, formatter);
   }

   public Formatter removeFormatter(Class clazz) {
      return (Formatter)formatters.remove(clazz);
   }

   public Formatter getFormatter(Class clazz) {
      return (Formatter)formatters.get(clazz);
   }

   public Map getFormatters() {
      return new HashMap(formatters);
   }

   protected void registerFormatters() {
      log.info("Registering formatters"); // NOI18N

      for (final Iterator i = formatters.entrySet().iterator(); i.hasNext(); ) {
         Map.Entry entry = (Map.Entry)i.next();

         FormatterRegistry.getInstance().register((Class)entry.getKey(),
               (Formatter)entry.getValue());
      }
   }

   public boolean isRunNoopCommand() {
      return runNoopCommand;
   }

   public void setRunNoopCommand(boolean runNoopCommand) {
      this.runNoopCommand = runNoopCommand;
   }

   protected void runNoopCommand() {
      if (!isRunNoopCommand()) {
         return;
      }

      Thread.ofVirtual().name("NoopCommand-Daemon").start(() -> { // NOI18N
         new NoopCommand().remotable();
         log.info("Noop command executed"); // NOI18N
      });
   }

   public boolean isLoadValidatorRules() {
      return loadValidatorRules;
   }

   public void setLoadValidatorRules(boolean loadValidatorRules) {
      this.loadValidatorRules = loadValidatorRules;
   }

   protected void loadValidatorRules() {
      if (!isLoadValidatorRules()) {
         return;
      }

      Thread.ofVirtual().name("ValidatorRulesLoader-Daemon").start(() -> { // NOI18N
         ValidationUtils.getInstance();
         log.info("Validation rules loaded"); // NOI18N
      });
   }

   public void initialize() {
      registerBeanUtilsBean();
      registerJXPathContextFactory();
      registerConverters();
      registerFormatters();
      runNoopCommand();
      loadValidatorRules();

      log.info("Initialization started"); // NOI18N
   }
}