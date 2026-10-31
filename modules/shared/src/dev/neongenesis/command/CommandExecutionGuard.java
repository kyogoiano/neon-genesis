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
package dev.neongenesis.command;

public final class CommandExecutionGuard {
   private static final ThreadLocal<Integer> DEPTH = new ThreadLocal<Integer>();

   private CommandExecutionGuard() {
   }

   public static boolean isExecuting() {
      return DEPTH.get() != null;
   }

   public static void enter() {
      Integer depth = DEPTH.get();
      DEPTH.set(depth == null ? 1 : depth + 1);
   }

   public static void exit() {
      Integer depth = DEPTH.get();
      if (depth == null || depth <= 1) {
         DEPTH.remove();
      } else {
         DEPTH.set(depth - 1);
      }
   }
}
