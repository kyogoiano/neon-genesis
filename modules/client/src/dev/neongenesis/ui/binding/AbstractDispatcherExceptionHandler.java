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
package dev.neongenesis.ui.binding;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import dev.neongenesis.ui.UIException;
import dev.neongenesis.ui.UIUtils;
import dev.neongenesis.ui.ValidationException;

import dev.neongenesis.util.Bundle;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractDispatcherExceptionHandler implements
      DispatcherExceptionHandler {
   public void handleException(Throwable throwable) {
      if (throwable instanceof InvocationTargetException) {
         handleException(((InvocationTargetException)throwable).getTargetException());

         return;
      } else if (throwable instanceof ValidationException) {
         showValidationErrors((ValidationException) throwable);

         return;
      } else if (throwable instanceof UIException) {
         handleUIException((UIException) throwable);

         return;
      }

      try {
         if (handleCustomException(throwable)) {
            return;
         }
      } catch (Throwable t) {
         LogFactory.getLog(getClass()).error("Unknown exception", t); // NOI18N
      }

      handleUnknownException(throwable);
   }
   
   public boolean handleCustomException(Throwable t) throws Exception {
      if (t.getCause() != null) {
         handleException(t.getCause());

         return true;
      }

      return false;
   }

   public void handleUIException(UIException uiException) {
      showWarningMessageDialog(uiException.getTitle(), uiException
            .getDescription());
   }

   public void handleUnknownException(Throwable t) {
      handleException(Bundle.getMessage(AbstractDispatcherExceptionHandler.class,
            "UNEXPECTED_ERROR_OCCURRED"), t); // NOI18N
   }

   public void showValidationErrors(ValidationException ve) {
      final StringBuffer displayMessage = new StringBuffer();

      for (final Iterator messages = ve.getValidationErrors().values()
            .iterator(); messages.hasNext();) {
         if (displayMessage.length() != 0) {
            displayMessage.append('\n');
         }

         displayMessage.append(messages.next().toString());
      }

      showWarningMessageDialog(UIUtils.getInstance().getBundle().getString(
            "validation.errors.title"), displayMessage.toString()); // NOI18N
   }

   public String getErrorMessage() {
      return Bundle.getMessage(AbstractDispatcherExceptionHandler.class, "ERROR"); // NOI18N
   }

   public void handleException(String message, Throwable throwable) {
      showErrorMessageDialog(getErrorMessage(), message, throwable);

      LogFactory.getLog(getClass()).error(message, throwable);
   }

   protected String getStackTrace(Throwable throwable) {
      return UIUtils.getInstance().getStackTrace(throwable);
   }

   protected abstract void showWarningMessageDialog(final String title, final String message);
   protected abstract void showErrorMessageDialog(final String title, final String message, final Throwable throwable);
}
