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

import dev.neongenesis.cloning.Cloner;
import dev.neongenesis.cloning.ClonerRegistry;
import dev.neongenesis.equality.EqualityComparator;
import dev.neongenesis.equality.EqualityComparatorRegistry;
import dev.neongenesis.registry.DefaultValueRegistry;
import dev.neongenesis.resolvers.EmptyResolver;
import dev.neongenesis.resolvers.EmptyResolverRegistry;
import dev.neongenesis.script.ScriptExpression;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FieldMetadata extends MemberMetadata {
   private static final Log log = LogFactory.getLog(FieldMetadata.class);

   private final String fieldName;
   private final Class fieldClass;
   private final boolean writeable;

   private Converter converter;
   private ScriptExpression clearOnCondition;
   private EqualityComparator equalityComparator;
   private EmptyResolver emptyResolver;
   private Cloner cloner;
   private Object emptyValue;

   private boolean processed = false;

   public FieldMetadata(String fieldName, Class fieldClass, boolean writeable) {
      this.fieldName = fieldName;
      this.fieldClass = fieldClass;
      this.writeable = writeable;
   }

   public String getFieldName() {
      return fieldName;
   }

   public String getName() {
      return getFieldName();
   }

   public boolean isWriteable() {
      return writeable;
   }

   public ScriptExpression getClearOnCondition() {
      return clearOnCondition;
   }

   public void setClearOnCondition(ScriptExpression clearOnCondition) {
      this.clearOnCondition = clearOnCondition;
   }

   public EmptyResolver getEmptyResolver() {
      if (!processed) {
         internalProcess();
      }
      return emptyResolver;
   }

   public void setEmptyResolver(EmptyResolver emptyResolver) {
      this.emptyResolver = emptyResolver;
   }

   public EqualityComparator getEqualityComparator() {
      if (!processed) {
         internalProcess();
      }
      return equalityComparator;
   }

   public void setEqualityComparator(EqualityComparator equalityComparator) {
      this.equalityComparator = equalityComparator;
   }

   public Converter getConverter() {
      if (!processed) {
         internalProcess();
      }
      return converter;
   }

   public void setConverter(Converter converter) {
      this.converter = converter;
   }

   public Class getFieldClass() {
      return fieldClass;
   }

   public Object getEmptyValue() {
      if (!processed) {
         internalProcess();
      }
      return emptyValue;
   }

   public void setEmptyValue(Object emptyValue) {
      this.emptyValue = emptyValue;
   }

   public Cloner getCloner() {
      if (!processed) {
         internalProcess();
      }
      return cloner;
   }

   public void setCloner(Cloner cloner) {
      this.cloner = cloner;
   }

   private void internalProcess() {
      if (equalityComparator == null) {
         equalityComparator = EqualityComparatorRegistry.getInstance()
               .getDefaultEqualityComparatorFor(getFieldClass());
      }
      if (emptyResolver == null) {
         emptyResolver = EmptyResolverRegistry.getInstance()
               .getDefaultEmptyResolverFor(getFieldClass());
         emptyValue = DefaultValueRegistry.getInstance().get(getFieldClass(),
               true);
      }
      if (converter == null) {
         converter = ConvertUtils.lookup(getFieldClass());
         
         if (converter == null) {
            log.warn("Field " + fieldName + " in " + fieldClass.getName() + // NOI18N
                  " does not have an associated converter; perhaps " + // NOI18N
                  "new StartupHelper().initialize() hasn't been invoked?"); // NOI18N
         }
      }
      if (cloner == null) {
         cloner = ClonerRegistry.getInstance().getDefaultClonerFor(
               getFieldClass());
      }
      processed = true;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(getClass().getName());
      buffer.append("."); // NOI18N
      buffer.append(fieldName);
      buffer.append(" = {\n\t\tenabledCondition = "); // NOI18N
      buffer.append(getEnabledCondition());
      buffer.append(" = {\n\t\tvisibleCondition = "); // NOI18N
      buffer.append(getVisibleCondition());
      buffer.append("\n\t\tclearOnCondition = "); // NOI18N
      buffer.append(clearOnCondition);
      buffer.append("\n\t\tequalityComparator = "); // NOI18N
      buffer.append(equalityComparator);
      buffer.append("\n\t\temptyResolver = "); // NOI18N
      buffer.append(emptyResolver);
      buffer.append("\n\t\tconverter = "); // NOI18N
      buffer.append(converter);
      buffer.append("\n\t\tcloner = "); // NOI18N
      buffer.append(cloner);
      buffer.append("\n\t\tfieldClass = "); // NOI18N
      buffer.append(fieldClass);
      buffer.append("\n\t\temptyValue = "); // NOI18N
      buffer.append(emptyValue);
      buffer.append("\n\t}"); // NOI18N
      return buffer.toString();
   }
}