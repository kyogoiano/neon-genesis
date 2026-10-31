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
package dev.neongenesis.ui.paging;

import dev.neongenesis.annotation.Action;
import dev.neongenesis.annotation.VisibleWhen;
import dev.neongenesis.paging.Page;
import dev.neongenesis.ui.BaseForm;

public abstract class BaseSearchForm extends BaseForm {
   private int pageNumber;
   private boolean lastPage = true;
   private boolean runSearch;
   private boolean resetSearch = true;
   private Page page;

   protected abstract Page performSearch() throws Exception;

   public void search() throws Exception {
      if (isResetSearch()) {
         setPageNumber(0);
      }

      setPage(performSearch());

      setPageNumber(getPage().getPageNumber());
      setLastPage(getPage().isLast());
      setRunSearch(false);
      setResetSearch(true);
   }

   @Action
   @VisibleWhen({
                  "beanshell", "form.getPageNumber() > 0",
                  "javascript", "form.pageNumber > 0",
                  "jxpath", "pageNumber > 0",
                  "el", "${form.pageNumber > 0}"
                })
   public void previousPage() {
      setPageNumber(getPageNumber() - 1);
      setRunSearch(true);
      setResetSearch(false);
   }

   @Action
   @VisibleWhen({
               "beanshell", "!form.isLastPage()",
               "javascript", "!form.lastPage",
               "jxpath", "lastPage=false()",
               "el", "${form.lastPage}"
               })
   public void nextPage() {
      pageNumber++;
      setRunSearch(true);
      setResetSearch(false);
   }

   public int getPageNumber() {
      return pageNumber;
   }

   protected void setPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
   }

   public boolean isLastPage() {
      return lastPage;
   }

   protected void setLastPage(boolean lastPage) {
      this.lastPage = lastPage;
   }

   public boolean isRunSearch() {
      return runSearch;
   }

   protected void setRunSearch(boolean runSearch) {
      this.runSearch = runSearch;
   }

   protected boolean isResetSearch() {
      return resetSearch;
   }

   protected void setResetSearch(boolean resetSearch) {
      this.resetSearch = resetSearch;
   }

   protected Page getPage() {
      return page;
   }

   protected void setPage(Page page) {
      this.page = page;
   }
}