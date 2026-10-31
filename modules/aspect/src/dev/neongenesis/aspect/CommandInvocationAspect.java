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
package dev.neongenesis.aspect;

import java.lang.reflect.Method;
import java.io.Serializable;

import dev.neongenesis.annotation.Remotable;
import dev.neongenesis.annotation.Transactional;
import dev.neongenesis.command.Query;
import dev.neongenesis.command.Transaction;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareMixin;

@Aspect
public class CommandInvocationAspect {
   @DeclareMixin("dev.neongenesis.command.Command+")
   public static CommandResolver createCommandResolver() {
      return new CommandResolverImpl();
   }

   public static boolean isRemotable(Method method) {
      return !"false".equals(System.getProperty("genesis.aspect.useFastMode",
            "true"))
            || method.getAnnotation(Remotable.class) != null
            || Query.class.isAssignableFrom(method.getDeclaringClass())
            || isTransactional(method);
   }

   public static boolean isTransactional(Method method) {
      return method.getAnnotation(Transactional.class) != null
            || Transaction.class.isAssignableFrom(method.getDeclaringClass());
   }

   public static class CommandResolverImpl implements CommandResolver,
         Serializable {
      private static final long serialVersionUID = 1L;

      public boolean isRemotable(Method method) {
         return CommandInvocationAspect.isRemotable(method);
      }

      public boolean isTransactional(Method method) {
         return CommandInvocationAspect.isTransactional(method);
      }
   }
}
