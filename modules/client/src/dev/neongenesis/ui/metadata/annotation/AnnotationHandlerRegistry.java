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
package dev.neongenesis.ui.metadata.annotation;

import java.util.HashMap;
import java.util.Map;

import dev.neongenesis.annotation.CallWhen;
import dev.neongenesis.annotation.ClearOn;
import dev.neongenesis.annotation.Cloner;
import dev.neongenesis.annotation.Condition;
import dev.neongenesis.annotation.DataProvider;
import dev.neongenesis.annotation.EmptyResolver;
import dev.neongenesis.annotation.EmptyValue;
import dev.neongenesis.annotation.EnabledWhen;
import dev.neongenesis.annotation.EqualityComparator;
import dev.neongenesis.annotation.ValidateBefore;
import dev.neongenesis.annotation.VisibleWhen;

public class AnnotationHandlerRegistry {
   private static final AnnotationHandlerRegistry instance = new AnnotationHandlerRegistry();
   private Map registry = new HashMap();

   private AnnotationHandlerRegistry() {
      registry.put(Condition.class, new ConditionAnnotationHandler());
      registry.put(EnabledWhen.class, new EnabledWhenAnnotationHandler());
      registry.put(VisibleWhen.class, new VisibleWhenAnnotationHandler());
      registry.put(ValidateBefore.class, new ValidateBeforeAnnotationHandler());
      registry.put(DataProvider.class, new DataProviderAnnotationHandler());
      registry.put(CallWhen.class, new CallWhenAnnotationHandler());
      registry.put(ClearOn.class, new ClearOnAnnotationHandler());
      registry.put(EqualityComparator.class,
            new EqualityComparatorAnnotationHandler());
      registry.put(EmptyResolver.class, new EmptyResolverAnnotationHandler());
      registry.put(EmptyValue.class, new EmptyValueAnnotationHandler());
      registry.put(Cloner.class, new ClonerAnnotationHandler());
   }

   public static AnnotationHandlerRegistry getInstance() {
      return instance;
   }

   public AnnotationHandler get(Class annotationClass) {
      return (AnnotationHandler) registry.get(annotationClass);
   }
}
