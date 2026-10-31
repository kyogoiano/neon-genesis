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
package dev.neongenesis.tests.hibernate;

import dev.neongenesis.paging.Page;
import dev.neongenesis.tests.TestCase;


public class PagingTest extends TestCase {

	private final DbActions dbActions = new DbActions();

	public void tearDown() throws Exception {
		dbActions.deleteAll();
	}

	public void testEmptyPage() throws Exception {
		Page page = dbActions.getPageUsingCriteria(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingCriteria(4, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(4, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertTrue(page.getResults().isEmpty());
		assertTrue(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testOnePage() throws Exception {
		dbActions.insert(7);
		Page page = dbActions.getPageUsingCriteria(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingCriteria(19, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(19, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testMoreThanOnePage() throws Exception {
		dbActions.insert(17);
		Page page = dbActions.getPageUsingCriteria(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingCriteria(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingQuery(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 7);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testExactlyTwoPages() throws Exception {
		dbActions.insert(20);
		Page page = dbActions.getPageUsingCriteria(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingCriteria(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingCriteria(2, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(0, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertTrue(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingQuery(1, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertFalse(page.isLast());
		page = dbActions.getPageUsingQuery(2, 10);
		assertEquals(page.getPageNumber(), 1);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 10);
		assertFalse(page.isFirst());
		assertTrue(page.isLast());
	}

	public void testPageNotExists() throws Exception {
		Page page = dbActions.getPageUsingCriteria(4, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 0);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(4, 10);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), 10);
		assertEquals(page.getResults().size(), 0);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());
	}

   public void testNegativePageNumbers() throws Exception {
      int instanceCount = 5;
      int resultsPerPage = 10;

      dbActions.insert(instanceCount);

      Page page = dbActions.getPageUsingCriteria(-1, resultsPerPage);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), resultsPerPage);
		assertEquals(page.getResults().size(), instanceCount);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());

		page = dbActions.getPageUsingQuery(-1, resultsPerPage);
		assertEquals(page.getPageNumber(), 0);
		assertEquals(page.getResultsPerPage(), resultsPerPage);
		assertEquals(page.getResults().size(), instanceCount);
		assertTrue(page.isFirst());
		assertTrue(page.isLast());
   }
}