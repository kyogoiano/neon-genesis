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
package dev.neongenesis.ui.metadata;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import dev.neongenesis.reflection.MethodEntry;
import dev.neongenesis.script.Script;
import dev.neongenesis.script.ScriptExpression;

public class FormMetadata {
   private final Class formClass;
   private final Script script;

   private final Map namedConditions;
   private final Map fieldMetadatas;
   private final Map methodMetadatas;
   private Map actionMetadatas;
   private Map dataProviderMetadatas;
   private Map dataProviderIndexes;

   public FormMetadata(final Class formClass, final Script script) {
      this.formClass = formClass;
      this.script = script;
      this.namedConditions = new HashMap();
      this.fieldMetadatas = new HashMap();
      this.methodMetadatas = new HashMap();
   }

   public Class getFormClass() {
      return formClass;
   }

   public Script getScript() {
      return script;
   }

   public Map getFieldMetadatas() {
      return new HashMap(fieldMetadatas);
   }

   public Map getNamedConditions() {
      return new HashMap(namedConditions);
   }

   public Map getMethodMetadatas(){
      return new HashMap(methodMetadatas);
   }

   public Map getActionMetadatas() {
      if (actionMetadatas == null) {
         actionMetadatas = new HashMap();
         for (Iterator iter = methodMetadatas.values().iterator(); iter.hasNext();) {
            MethodMetadata methodMeta = (MethodMetadata) iter.next();
            if (methodMeta.getActionMetadata() == null) {
               continue;
            }
            actionMetadatas.put(methodMeta.getMethodEntry(), methodMeta.getActionMetadata());
         }
      }
      return new HashMap(actionMetadatas);
   }

   public Map getDataProviderMetadatas() {
      if (dataProviderMetadatas == null || dataProviderIndexes == null) {
         initializeDataProviders();
      }
      return new HashMap(dataProviderMetadatas);
   }

   public Map getDataProviderIndexes() {
      if (dataProviderMetadatas == null || dataProviderIndexes == null) {
         initializeDataProviders();
      }
      return new HashMap(dataProviderIndexes);
   }

   private void initializeDataProviders() {
      dataProviderMetadatas = new HashMap();
      dataProviderIndexes = new HashMap();

      MethodMetadata methodMeta;
      DataProviderMetadata dataMeta;

      for (Iterator iter = methodMetadatas.values().iterator(); iter.hasNext();) {
         methodMeta = (MethodMetadata) iter.next();
         dataMeta = methodMeta.getDataProviderMetadata();

         if (dataMeta == null) {
            continue;
         }

         dataProviderMetadatas.put(methodMeta.getMethodEntry(), dataMeta);

         if (dataMeta.getIndexField() == null) {
            continue;
         }

         dataProviderIndexes.put(dataMeta.getIndexField().getFieldName(), dataMeta);
      }
   }

   public ScriptExpression getNamedCondition(final String namedConditionName) {
      return (ScriptExpression) namedConditions.get(namedConditionName);
   }

   public FieldMetadata getFieldMetadata(final String fieldName) {
      return (FieldMetadata) fieldMetadatas.get(fieldName);
   }
   
   public MethodMetadata getMethodMetadata(final MethodEntry methodEntry) {
      return (MethodMetadata) methodMetadatas.get(methodEntry);
   }
   
   public MethodMetadata getMethodMetadata(final String methodName) {
      return (MethodMetadata) methodMetadatas.get(new MethodEntry(methodName, new String[0]));
   }

   public DataProviderMetadata getDataProviderForIndex(final String fieldName) {
      return (DataProviderMetadata) dataProviderIndexes.get(fieldName);
   }

   public void addNamedCondition(final String key, final ScriptExpression value) {
      namedConditions.put(key, value);
   }

   public void addFieldMetadata(final String fieldName,
         final FieldMetadata fieldMetadata) {
      fieldMetadatas.put(fieldName, fieldMetadata);
   }
   
   public void addMethodMetadata(final Method method, final MethodMetadata methodMetadata) {
      methodMetadatas.put(new MethodEntry(method), methodMetadata);
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClass().getName());
      buffer.append(" = {\n"); // NOI18N

      for (Iterator iter = namedConditions.entrySet().iterator(); iter
            .hasNext();) {
         Map.Entry element = (Map.Entry) iter.next();
         buffer.append("\t"); // NOI18N
         buffer.append(element.getKey());
         buffer.append("="); // NOI18N
         buffer.append(element.getValue());
         buffer.append("\n"); // NOI18N
      }

      for (Iterator iter = fieldMetadatas.values().iterator(); iter.hasNext();) {
         buffer.append("\t"); // NOI18N
         buffer.append(iter.next());
         buffer.append("\n"); // NOI18N
      }
      
      for (Iterator iter = methodMetadatas.values().iterator(); iter.hasNext();) {
         buffer.append("\t"); // NOI18N
         buffer.append(iter.next());
         buffer.append("\n"); // NOI18N
      }

      buffer.append("}"); // NOI18N

      return buffer.toString();
   }
}