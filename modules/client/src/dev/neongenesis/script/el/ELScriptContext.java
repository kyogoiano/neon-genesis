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
package dev.neongenesis.script.el;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.beans.FeatureDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.el.ArrayELResolver;
import jakarta.el.BeanELResolver;
import jakarta.el.CompositeELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELException;
import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.FunctionMapper;
import jakarta.el.ListELResolver;
import jakarta.el.MapELResolver;
import jakarta.el.ValueExpression;
import jakarta.el.VariableMapper;

import org.apache.taglibs.standard.functions.Functions;

import dev.neongenesis.script.PrimitiveFunctions;
import dev.neongenesis.script.ScriptContext;
import dev.neongenesis.script.ScriptException;
import dev.neongenesis.script.ScriptExpression;
import dev.neongenesis.script.ScriptFunctionsAdapter;

public class ELScriptContext extends ScriptContext {
   public static final String FORM_NS = "form"; // NOI18N
   public static final String GENESIS_FUNCTIONS_NS = "g"; // NOI18N

   private final Map variables = new HashMap();
   private final Map unmodifiableMap = Collections.unmodifiableMap(variables);
   private final Map functions = new HashMap();
   private final Map<String, ValueExpression> expressions =
         new ConcurrentHashMap<String, ValueExpression>();
   private final ExpressionFactory expressionFactory;
   private final ELContext elContext;

   protected ELScriptContext(Object root, ExpressionFactory expressionFactory) {
      this.expressionFactory = expressionFactory;
      this.elContext = new GenesisELContext();
      variables.put(FORM_NS, root);
      registerFunctions("", Functions.class); // NOI18N
      registerFunctions("", PrimitiveFunctions.class); // NOI18N
      registerFunctions(GENESIS_FUNCTIONS_NS, getFunctions());
   }

   protected Object doEval(ScriptExpression expr) {
      try {
         String expression = expr.getExpressionString();
         ValueExpression valueExpression = expressions.get(expression);
         if (valueExpression == null) {
            valueExpression = expressionFactory.createValueExpression(
                  elContext, expression, Object.class);
            expressions.put(expression, valueExpression);
         }
         return valueExpression.getValue(elContext);
      } catch (ELException e) {
         throw new ScriptException(e.getMessage(), e);
      }
   }

   protected ScriptExpression newScriptExpression(String expression) {
      return new ELExpression(expression);
   }

   public void registerFunctions(String prefix, Class functionClass) {
      Method methods[] = functionClass.getMethods();
      for (int i = 0; i < methods.length; i++) {
         Method m = methods[i];
         if (Modifier.isStatic(m.getModifiers())) {
            functions.put(prefix + ':' + m.getName(), m);
         }
      }
      expressions.clear();
   }

   public Method resolveFunction(String prefix, String localName) {
      return (Method)functions.get(prefix + ':' + localName);
   }

   public void declare(String name, Object value) {
      variables.put(name, value);
   }

   public Object lookup(String name) {
      return variables.get(name);
   }

   public void undeclare(String name) {
      variables.remove(name);
   }

   public Map getContextMap() {
      return unmodifiableMap;
   }

   protected Class getFunctions() {
      return ScriptFunctionsAdapter.class;
   }

   private class GenesisELContext extends ELContext {
      private final CompositeELResolver resolver = new CompositeELResolver();
      private final FunctionMapper functionMapper = new FunctionMapper() {
         public Method resolveFunction(String prefix, String localName) {
            return ELScriptContext.this.resolveFunction(prefix, localName);
         }
      };

      private GenesisELContext() {
         putContext(ExpressionFactory.class, expressionFactory);
         resolver.add(new VariableELResolver());
         resolver.add(new MapELResolver());
         resolver.add(new ListELResolver());
         resolver.add(new ArrayELResolver());
         resolver.add(new BeanELResolver());
      }

      public ELResolver getELResolver() {
         return resolver;
      }

      public FunctionMapper getFunctionMapper() {
         return functionMapper;
      }

      public VariableMapper getVariableMapper() {
         return null;
      }

      public <T> T convertToType(Object value, Class<T> targetType) {
         return expressionFactory.coerceToType(value, targetType);
      }
   }

   private class VariableELResolver extends ELResolver {
      public Object getValue(ELContext context, Object base, Object property) {
         if (base == null && variables.containsKey(property)) {
            context.setPropertyResolved(base, property);
            return variables.get(property);
         }
         return null;
      }

      public Class<?> getType(ELContext context, Object base, Object property) {
         if (base == null && variables.containsKey(property)) {
            context.setPropertyResolved(base, property);
            Object value = variables.get(property);
            return value == null ? Object.class : value.getClass();
         }
         return null;
      }

      public void setValue(ELContext context, Object base, Object property,
            Object value) {
         if (base == null && variables.containsKey(property)) {
            context.setPropertyResolved(base, property);
            variables.put(property, value);
         }
      }

      public boolean isReadOnly(ELContext context, Object base,
            Object property) {
         if (base == null && variables.containsKey(property)) {
            context.setPropertyResolved(base, property);
            return false;
         }
         return false;
      }

      public Class<?> getCommonPropertyType(ELContext context, Object base) {
         return base == null ? String.class : null;
      }
   }
}