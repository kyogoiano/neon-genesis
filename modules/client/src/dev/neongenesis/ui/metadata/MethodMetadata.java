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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dev.neongenesis.reflection.MethodEntry;
import dev.neongenesis.reflection.ReflectionInvoker;
import dev.neongenesis.script.ScriptExpression;

public final class MethodMetadata {
   private final static Object[] emptyObjectArray = new Object[0];
   private final static String[] emptyStringArray = new String[0];

   private final MethodEntry methodEntry;
   private final ActionMetadata actionMetadata;
   private final DataProviderMetadata dataProviderMetadata;
   private ScriptExpression callCondition;

   public MethodMetadata(Method method, boolean isAction, boolean isProvider) {
      this.methodEntry = new MethodEntry(method);
      this.actionMetadata = isAction ? new ActionMetadata(getName()) : null;
      this.dataProviderMetadata = isProvider ? new DataProviderMetadata(method) : null;
   }

   public String getName() {
      return getMethodEntry().getMethodName();
   }

   public MethodEntry getMethodEntry() {
      return methodEntry;
   }

   public ScriptExpression getCallCondition() {
      return callCondition;
   }

   public void setCallCondition(ScriptExpression callWhen) {
      this.callCondition = callWhen;
   }

   public ActionMetadata getActionMetadata() {
      return actionMetadata;
   }

   public DataProviderMetadata getDataProviderMetadata() {
      return dataProviderMetadata;
   }

   public Object invoke(final Object target) throws NoSuchMethodException,
         IllegalAccessException, ClassNotFoundException,
         InvocationTargetException {
      return ReflectionInvoker.getInstance().invoke(target,
            methodEntry.getMethodName(), emptyStringArray, emptyObjectArray);
   }
}