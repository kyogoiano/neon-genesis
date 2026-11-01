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
import java.util.HashMap;
import java.util.Map;
import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.script.ScriptRegistry;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Var;

public class BasicValidatorTest extends GenesisTestCase {
   private static final String BIG_DECIMAL_PROPERTY = "bigDecimal";

   private Field field;
   private Var min = new Var("min", "0.1", null);
   private Var max = new Var("max", "10.1", null);
   private Var script = new Var("script", "form.getDependency() == '10'", null);
   private Converter oldConverter;

   protected void setUp() {
      field = new Field();
      field.setProperty(BIG_DECIMAL_PROPERTY);

      oldConverter = ConvertUtils.lookup(BigDecimal.class);
      ConvertUtils.register(new BigDecimalConverter(null), BigDecimal.class);
   }

   protected void tearDown() {
      field = null;
      ConvertUtils.register(oldConverter, BigDecimal.class);
   }

   public void testValidateBigDecimalRange() {
      field.addVar(min);
      field.addVar(max);

      Map map = new HashMap();
      map.put(BIG_DECIMAL_PROPERTY, null);
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "-1");
      assertFalse(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0");
      assertFalse(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0.1");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "1");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1");
      assertTrue(BasicValidator.validateBigDecimalRange(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1000000000000001");
      assertFalse(BasicValidator.validateBigDecimalRange(map, field));
   }

   public void testValidateMin() {
      field.addVar(min);

      Map map = new HashMap();
      map.put(BIG_DECIMAL_PROPERTY, null);
      assertTrue(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "");
      assertTrue(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "-1");
      assertFalse(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0");
      assertFalse(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "0.1");
      assertTrue(BasicValidator.validateMin(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "1");
      assertTrue(BasicValidator.validateMin(map, field));
   }

   public void testValidateMax() {
      field.addVar(max);

      Map map = new HashMap();
      map.put(BIG_DECIMAL_PROPERTY, null);
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "");
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "1");
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1");
      assertTrue(BasicValidator.validateMax(map, field));

      map.put(BIG_DECIMAL_PROPERTY, "10.1000000000000001");
      assertFalse(BasicValidator.validateMax(map, field));
   }

   public void testValidateScript() {
      ScriptRegistry.getInstance().setScriptFactoryName(
            ScriptRegistry.JAVASCRIPT);
      ScriptRegistry.getInstance().initialize();
      field.addVar(script);
      ScriptForm form = new ScriptForm("9");
      assertFalse(BasicValidator.validateScript(form, field));

      form.setDependency("10");
      assertTrue(BasicValidator.validateScript(form, field));
   }

   public static class ScriptForm {
      private String dependency;

      public ScriptForm(String dependency) {
         this.dependency = dependency;
      }

      public String getDependency() {
         return dependency;
      }

      public void setDependency(String dependency) {
         this.dependency = dependency;
      }
   }
}
