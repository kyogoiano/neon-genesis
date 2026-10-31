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

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import dev.neongenesis.command.hibernate.CriteriaResolver;
import dev.neongenesis.command.hibernate.HibernateCriteria;
import dev.neongenesis.resolvers.EmptyResolver;
import dev.neongenesis.resolvers.EmptyResolverRegistry;
import dev.neongenesis.ui.metadata.DefaultFormMetadataFactory;
import dev.neongenesis.ui.metadata.FieldMetadata;
import dev.neongenesis.ui.metadata.FormMetadata;
import dev.neongenesis.ui.metadata.FormMetadataFactory;
import dev.neongenesis.util.Bundle;
import dev.neongenesis.util.GenesisUtils;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CriteriaPropertyHelper {
   private static final Log log = LogFactory.getLog(CriteriaPropertyHelper.class);

   private CriteriaPropertyHelper() {
   }

   public static void fillCriteria(HibernateCriteria hibCriteria, Object form)
         throws Exception {
      final CriteriaResolver critResolver = (CriteriaResolver) hibCriteria;

      final FormMetadata formMeta;
      if (form instanceof FormMetadataFactory) {
         formMeta = ((FormMetadataFactory)form).getFormMetadata(form.getClass());
      } else {
         formMeta = new DefaultFormMetadataFactory().getFormMetadata(form.getClass());
      }

      final Map propertiesMap = PropertyUtils.describe(form);
      GenesisUtils.normalizeMap(propertiesMap);

      final PropertyDescriptor[] descriptors = PropertyUtils
            .getPropertyDescriptors(hibCriteria);
      final Collection criteriaPropertyNames = new ArrayList(descriptors.length);

      for (int i = 0; i < descriptors.length; i++) {
         criteriaPropertyNames.add(descriptors[i].getName());
      }

      Map.Entry entry;
      FieldMetadata fieldMeta;
      Object value;
      EmptyResolver emptyResolver;

      propertiesMap.keySet().retainAll(criteriaPropertyNames);

      for (Iterator iter = propertiesMap.entrySet().iterator(); iter.hasNext();) {
         entry = (Map.Entry) iter.next();
         fieldMeta = formMeta.getFieldMetadata(entry.getKey().toString());
         value = entry.getValue();

         emptyResolver = (fieldMeta == null) ? EmptyResolverRegistry
               .getInstance().getDefaultEmptyResolverFor((value == null ?
               Object.class : value.getClass())) : fieldMeta.getEmptyResolver();

         if (emptyResolver.isEmpty(value)) {
            iter.remove();
            continue;
         }
      }

      if (log.isDebugEnabled()) {
         log.debug(Bundle.getMessage(CriteriaPropertyHelper.class,
               "STORING_PROPERTIES_X_FOR_LATER_EXECUTION", propertiesMap)); // NOI18N
      }

      critResolver.setPropertiesMap(propertiesMap);
   }
}