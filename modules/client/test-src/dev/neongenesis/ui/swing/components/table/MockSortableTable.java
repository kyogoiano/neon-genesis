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
package dev.neongenesis.ui.swing.components.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;

public class MockSortableTable extends JComponent {
   private List model;
   private List view;

   public MockSortableTable() {
      this(Arrays.asList(4, 5, 2, 1,
              3));
   }

   public MockSortableTable(List model) {
      this.model = model;
      view = new ArrayList(model);
   }

   public int convertRowIndexToModel(int viewRowIndex) {
      return model.indexOf(view.get(viewRowIndex));
   }

   public int convertRowIndexToView(int modelRowIndex) {
      return view.indexOf(model.get(modelRowIndex));
   }

   public void sort() {
      Collections.sort(view);
   }
}
