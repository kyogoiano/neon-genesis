/*
 * Neon Genesis
 * Copyright (C) 2026 Leandro Cardoso de Andrade (@kyogoiano <leandro.ueg@gmail.com>)
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
package dev.neongenesis.ui.javafx.components;

import dev.neongenesis.ui.binding.BoundDataProvider;
import dev.neongenesis.ui.javafx.JavaFXBinder;
import dev.neongenesis.ui.metadata.DataProviderMetadata;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TableView;

/**
 * Binds an OpenJFX {@link TableView} to a {@code @DataProvider} method and its
 * optional {@code objectField}.
 */
public class TableViewNodeBinder extends AbstractNodeBinder {

   @Override
   public BoundDataProvider bind(JavaFXBinder binder, Node node,
         DataProviderMetadata dataProviderMetadata) {
      return new TableViewBoundDataProvider(binder, (TableView<Object>) node, dataProviderMetadata);
   }

   public class TableViewBoundDataProvider extends AbstractBoundNodeMember implements BoundDataProvider {
      private final TableView<Object> tableView;
      private final DataProviderMetadata dataProviderMetadata;
      private final ListChangeListener<Integer> listener;
      private boolean updating;

      public TableViewBoundDataProvider(JavaFXBinder binder, TableView<Object> tableView,
            DataProviderMetadata dataProviderMetadata) {
         super(binder, tableView);
         this.tableView = tableView;
         this.dataProviderMetadata = dataProviderMetadata;
         this.listener = change -> {
            if (!updating) {
               int[] selected = tableView.getSelectionModel().getSelectedIndices()
                     .stream().mapToInt(Integer::intValue).filter(i -> i >= 0).toArray();
               getBinder().updateFormSelection(this.dataProviderMetadata, selected);
            }
         };
         this.tableView.getSelectionModel().getSelectedIndices().addListener(this.listener);
      }

      @Override
      public void updateIndexes(int[] selected) {
         updating = true;
         try {
            tableView.getSelectionModel().clearSelection();
            if (selected != null) {
               for (int idx : selected) {
                  if (idx >= 0 && idx < tableView.getItems().size()) {
                     tableView.getSelectionModel().select(idx);
                  }
               }
            }
         } finally {
            updating = false;
         }
      }

      @Override
      public void updateList(List list) throws Exception {
         updating = true;
         try {
            tableView.getItems().clear();
            if (list != null) {
               tableView.getItems().addAll(list);
            }
            if (dataProviderMetadata.isResetSelection()) {
               tableView.getSelectionModel().clearSelection();
            }
         } finally {
            updating = false;
         }
      }

      @Override
      public void unbind() {
         tableView.getSelectionModel().getSelectedIndices().removeListener(listener);
      }
   }
}
