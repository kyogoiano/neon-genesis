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
package dev.neongenesis.cdi;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.HeuristicMixedException;
import jakarta.transaction.HeuristicRollbackException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.RollbackException;
import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

@ApplicationScoped
public class CDITransactionContext implements UserTransaction {
   private static final CDITransactionContext INSTANCE = new CDITransactionContext();
   private static final ThreadLocal<Integer> STATUS =
         ThreadLocal.withInitial(() -> Status.STATUS_NO_TRANSACTION);

   public static CDITransactionContext getInstance() {
      return INSTANCE;
   }

   public static void enterTransaction() {
      STATUS.set(Status.STATUS_ACTIVE);
   }

   public static void enterNonTransactional() {
      STATUS.set(Status.STATUS_NO_TRANSACTION);
   }

   public static void clear() {
      STATUS.remove();
   }

   @Override
   public void begin() throws NotSupportedException, SystemException {
      STATUS.set(Status.STATUS_ACTIVE);
   }

   @Override
   public void commit() throws RollbackException, HeuristicMixedException,
         HeuristicRollbackException, SecurityException, IllegalStateException,
         SystemException {
      if (STATUS.get() == Status.STATUS_MARKED_ROLLBACK) {
         STATUS.set(Status.STATUS_NO_TRANSACTION);
         throw new RollbackException("Transaction was marked for rollback");
      }
      STATUS.set(Status.STATUS_NO_TRANSACTION);
   }

   @Override
   public void rollback() throws IllegalStateException, SecurityException,
         SystemException {
      STATUS.set(Status.STATUS_NO_TRANSACTION);
   }

   @Override
   public void setRollbackOnly() throws IllegalStateException, SystemException {
      STATUS.set(Status.STATUS_MARKED_ROLLBACK);
   }

   @Override
   public int getStatus() throws SystemException {
      return STATUS.get();
   }

   @Override
   public void setTransactionTimeout(int seconds) throws SystemException {
   }
}
