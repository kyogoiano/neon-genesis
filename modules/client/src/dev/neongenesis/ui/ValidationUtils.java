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
package dev.neongenesis.ui;

import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import dev.neongenesis.text.FormatterRegistry;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.validator.Arg;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.xml.sax.SAXException;

public final class ValidationUtils {
   private static final Log log = LogFactory.getLog(ValidationUtils.class);
   private static ValidationUtils instance;
   private final ValidatorResources resources;
   
   private ValidationUtils() throws IOException, SAXException {
      resources = initResources();
      UIUtils.getInstance().getBundle();
   }
   
   public static ValidationUtils getInstance() {
      if (instance == null) {
         try {
            instance = new ValidationUtils();
         } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
         } catch (SAXException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
         }
      }

      return instance;
   }

   private ValidatorResources initResources() throws IOException, SAXException {
      final InputStream rules = Thread.currentThread().getContextClassLoader()
                                    .getResourceAsStream("validator-rules.xml"); // NOI18N
      final InputStream is = Thread.currentThread().getContextClassLoader()
                                       .getResourceAsStream("validation.xml"); // NOI18N
      
      try {
         return new ValidatorResources(new InputStream[] {rules, is});
      } finally {
         if (rules != null) {
            rules.close();
         }

         if (is != null) {
            is.close();
         }
      }
   }

   public ValidatorResources getResources() {
      return resources;
   }

   public Map getMessages(ValidatorResults results, String formName) {
      return getMessages(resources, results, formName);
   }

   public Map getMessages(ValidatorResources resources, ValidatorResults results, 
         String formName) {
      final Map messages = new HashMap();
      final Form form = resources.getForm(Locale.getDefault(), formName);

      String property;
      Field field;
      ValidatorResult result;
      String actionName;
      String[] args;
      String msg;
      Arg arg;
      int argsLength;

       for (String s : results.getPropertyNames()) {
           property = s;
           field = form.getField(property);
           result = results.getValidatorResult(property);

           for (final Iterator<String> actionNames = result.getActions();
                actionNames.hasNext(); ) {
               actionName = actionNames.next();

               if (result.isValid(actionName)) {
                   continue;
               }

               msg = field.getMsg(actionName);

               if (msg == null) {
                   msg = resources.getValidatorAction(actionName).getMsg();
               }

               msg = UIUtils.getInstance().getBundle().getString(msg);

               argsLength = field.getArgs(actionName).length;
               args = new String[argsLength];

               for (int i = 0; i < argsLength; i++) {
                   arg = field.getArg(actionName, i);

                   if (arg == null) {
                       break;
                   }

                   args[i] = arg.getKey();

                   if (arg.isResource()) {
                       args[i] = UIUtils.getInstance().getBundle().getString(args[i]);
                   }
               }

               messages.put(property, MessageFormat.format(msg, (Object[]) args));
               break;
           }
       }

      return messages;
   }

   @Deprecated
   public boolean isValid(dev.neongenesis.ui.Form f) throws ValidatorException {
      try {
         return f.validate(getPropertiesMap(f)).isEmpty();
      } catch (ValidatorException ve) {
         throw ve;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public boolean isValid(Object form) throws ValidatorException {
      try {
         return validate(form, form.getClass().getName()).isEmpty();
      } catch (ValidatorException ve) {
         throw ve;
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   public ValidatorResults validate(Object form, String formName) 
         throws ValidatorException {
      return validate(form, formName, getResources());
   }
      
   public ValidatorResults validate(Object form, String formName, 
         ValidatorResources resources) throws ValidatorException {
      final Validator validator = new Validator(resources, formName);
      validator.setOnlyReturnErrors(true);
      validator.setUseContextClassLoader(true);

      try {
         validator.setParameter(Validator.BEAN_PARAM, getPropertiesMap(form));
      } catch (Exception e) {
         final ValidatorException validatorException = new ValidatorException();
         validatorException.initCause(e);
         throw validatorException;
      }

      return validator.validate();
   }

   public Map getPropertiesMap(Object form) throws Exception {
      if (form instanceof Map) {
         return (Map)form;
      }

      Map ret = PropertyUtils.describe(form);
      Map.Entry entry;
      for (Iterator i = ret.entrySet().iterator(); i.hasNext();) {
         entry = (Map.Entry) i.next();
         if ("class".equals(entry.getKey())) { // NOI18N
            i.remove();
         }
         entry.setValue(FormatterRegistry.getInstance()
               .format(entry.getValue()));
      }
      return ret;
   }
}