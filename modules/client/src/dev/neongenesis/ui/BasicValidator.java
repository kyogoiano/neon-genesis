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

import java.math.BigDecimal;

import dev.neongenesis.script.Script;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptRegistry;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.util.ValidatorUtils;

public class BasicValidator {
   public static boolean validateRequired(Object bean, Field field) {
      return !GenericValidator.isBlankOrNull(ValidatorUtils.getValueAsString(
            bean, field.getProperty()));
   }
   
   public static boolean validateByte(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) ||
            GenericValidator.isByte(value);
   }

   public static boolean validateBigDecimal(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) ||
            ConvertUtils.convert(value, BigDecimal.class) != null;
   }
   
   public static boolean validateBigDecimalRange(Object bean, Field field) {
      return validateBigDecimalRange(bean, field, true, true);
   }

   public static boolean validateMin(Object bean, Field field) {
      return validateBigDecimalRange(bean, field, true, false);
   }

   public static boolean validateMax(Object bean, Field field) {
      return validateBigDecimalRange(bean, field, false, true);
   }

   private static boolean validateBigDecimalRange(Object bean, Field field, 
         boolean validateMin, boolean validateMax) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      if (GenericValidator.isBlankOrNull(value)) {
         return true;
      }

      if (!validateBigDecimal(bean, field)) {
         return false;
      }
      
      BigDecimal inputValue = normalize(ConvertUtils.convert(value,
            BigDecimal.class));
      BigDecimal min = validateMin ? new BigDecimal(field.getVarValue("min")) : // NOI18N
            null;
      BigDecimal max = validateMax ? new BigDecimal(field.getVarValue("max")) : // NOI18N
            null;

      return (!validateMin || min.compareTo(inputValue) <= 0) && 
            (!validateMax || max.compareTo(inputValue) >= 0);
   }
   
   private static BigDecimal normalize(Object value) {
      return value == null ? new BigDecimal(0) : (BigDecimal)value;
   }

   public static boolean validateShort(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isShort(
            value);
   }
   
   public static boolean validateInt(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isInt(
            value);
   }
   
   public static boolean validateLong(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isLong(
            value);
   }
   
   public static boolean validateFloat(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isFloat(
            value);
   }
   
   public static boolean validateDouble(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isDouble(
            value);
   }
   
   public static boolean validateEmail(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isEmail(
            value);
   }
   
   public static boolean validateMask(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator
            .matchRegexp(value, field.getVarValue("mask")); // NOI18N
   }
   
   public static boolean validateDate(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator.isDate(
            value, field.getVarValue("pattern"), false); // NOI18N
   }
   
   public static boolean validateMaxLength(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator
            .maxLength(value, Integer.parseInt(field.getVarValue("maxlength"))); // NOI18N
   }
   
   public static boolean validateMinLength(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || GenericValidator
            .minLength(value, Integer.parseInt(field.getVarValue("minlength"))); // NOI18N
   }
   
   public static boolean validateLongRange(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || 
            (GenericValidator.isLong(value) && GenericValidator.isInRange(
            Long.parseLong(value), Long.parseLong(field.getVarValue("min")), // NOI18N
            Long.parseLong(field.getVarValue("max")))); // NOI18N
   }
   
   public static boolean validateDoubleRange(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());
      
      return GenericValidator.isBlankOrNull(value) || 
            (GenericValidator.isDouble(value) && GenericValidator.isInRange(
            Double.parseDouble(value), Double.parseDouble(field.getVarValue(
            "min")), Double.parseDouble(field.getVarValue("max")))); // NOI18N
   }

   public static boolean validateScript(Object bean, Field field) {
      Script script = ScriptRegistry.getInstance().getScript();
      final ScriptContext ctx = script.newContext(bean);

      return !Boolean.FALSE.equals(ctx.eval(field.getVarValue("script"))); // NOI18N
   }

   public static boolean validateUrl(Object bean, Field field) {
      String value = ValidatorUtils.getValueAsString(bean, field.getProperty());

      return GenericValidator.isBlankOrNull(value) || GenericValidator.isUrl(
            value);
   }
}