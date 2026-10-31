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
import javafx.scene.control.ListView;

/**
 * Binds an OpenJFX {@link ListView} to a {@code @DataProvider} method and its
 * optional {@code objectField}.
 */
public class ListViewNodeBinder extends AbstractNodeBinder {

   @Override
   public BoundDataProvider bind(JavaFXBinder binder, Node node,
         DataProviderMetadata dataProviderMetadata) {
      return new ListViewBoundDataProvider(binder, (ListView<Object>) node, dataProviderMetadata);
   }

   public class ListViewBoundDataProvider extends AbstractBoundNodeMember implements BoundDataProvider {
      private final ListView<Object> listView;
      private final DataProviderMetadata dataProviderMetadata;
      private final ListChangeListener<Integer> listener;
      private boolean updating;

      public ListViewBoundDataProvider(JavaFXBinder binder, ListView<Object> listView,
            DataProviderMetadata dataProviderMetadata) {
         super(binder, listView);
         this.listView = listView;
         this.dataProviderMetadata = dataProviderMetadata;
         this.listener = change -> {
            if (!updating) {
               int[] selected = listView.getSelectionModel().getSelectedIndices()
                     .stream().mapToInt(Integer::intValue).filter(i -> i >= 0).toArray();
               getBinder().updateFormSelection(this.dataProviderMetadata, selected);
            }
         };
         this.listView.getSelectionModel().getSelectedIndices().addListener(this.listener);
      }

      @Override
      public void updateIndexes(int[] selected) {
         updating = true;
         try {
            listView.getSelectionModel().clearSelection();
            if (selected != null) {
               for (int idx : selected) {
                  if (idx >= 0 && idx < listView.getItems().size()) {
                     listView.getSelectionModel().select(idx);
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
            listView.getItems().clear();
            if (list != null) {
               listView.getItems().addAll(list);
            }
            if (dataProviderMetadata.isResetSelection()) {
               listView.getSelectionModel().clearSelection();
            }
         } finally {
            updating = false;
         }
      }

      @Override
      public void unbind() {
         listView.getSelectionModel().getSelectedIndices().removeListener(listener);
      }
   }
}
