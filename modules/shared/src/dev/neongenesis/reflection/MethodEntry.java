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
package dev.neongenesis.reflection;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodEntry implements Serializable {
   private final String methodName;
   private final String[] argsClassesNames;

   public MethodEntry(final String methodName, final String[] argsClassesNames) {
      this.methodName = methodName;
      this.argsClassesNames = argsClassesNames;
   }
   
   public MethodEntry(final Method method){
      final Class[] parameterTypes = method.getParameterTypes();
      final String[] classNames = new String[parameterTypes.length];

      for(int i = 0; i < parameterTypes.length; i++){
          classNames[i] = parameterTypes[i].getName();
      }
      this.methodName = method.getName();
      this.argsClassesNames = classNames;
   }

   public String[] getArgsClassesNames() {
      return argsClassesNames;
   }

   public String getMethodName() {
      return methodName;
   }

   public boolean equals(Object o) {
      MethodEntry that = (MethodEntry) o;

      return that.methodName.equals(this.methodName)
            && Arrays.equals(that.argsClassesNames, this.argsClassesNames);
   }

   public int hashCode() {
      return methodName.hashCode();
   }
   
   public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append(methodName);
      buffer.append("("); // NOI18N
      for (int i = 0; i < argsClassesNames.length; i++) {
         if(i > 0){
            buffer.append(","); // NOI18N
         }
         buffer.append(argsClassesNames[i]);
      }
      buffer.append(")"); // NOI18N
      return buffer.toString();
   }
}
