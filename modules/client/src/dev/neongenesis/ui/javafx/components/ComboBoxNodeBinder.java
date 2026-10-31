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
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

/**
 * Binds an OpenJFX {@link ComboBox} to a {@code @DataProvider} method and its
 * optional {@code objectField}.
 */
public class ComboBoxNodeBinder extends AbstractNodeBinder {

   @Override
   public BoundDataProvider bind(JavaFXBinder binder, Node node,
         DataProviderMetadata dataProviderMetadata) {
      return new ComboBoxBoundDataProvider(binder, (ComboBox<Object>) node, dataProviderMetadata);
   }

   public class ComboBoxBoundDataProvider extends AbstractBoundNodeMember implements BoundDataProvider {
      private final ComboBox<Object> comboBox;
      private final DataProviderMetadata dataProviderMetadata;
      private final ChangeListener<Number> listener;
      private boolean updating;

      public ComboBoxBoundDataProvider(JavaFXBinder binder, ComboBox<Object> comboBox,
            DataProviderMetadata dataProviderMetadata) {
         super(binder, comboBox);
         this.comboBox = comboBox;
         this.dataProviderMetadata = dataProviderMetadata;
         this.listener = (obs, oldIdx, newIdx) -> {
            if (!updating) {
               int idx = newIdx == null ? -1 : newIdx.intValue();
               int[] indexes = getIndexes(idx);
               getBinder().updateFormSelection(this.dataProviderMetadata, indexes);
            }
         };
         this.comboBox.getSelectionModel().selectedIndexProperty().addListener(this.listener);
      }

      private int[] getIndexes(int selectedIndex) {
         if (selectedIndex < 0) {
            return new int[0];
         }
         if (isBlank(comboBox)) {
            return selectedIndex == 0 ? new int[0] : new int[] { selectedIndex - 1 };
         }
         return new int[] { selectedIndex };
      }

      @Override
      public void updateIndexes(int[] selected) {
         updating = true;
         try {
            if (selected == null || selected.length == 0 || selected[0] < 0) {
               if (isBlank(comboBox) && !comboBox.getItems().isEmpty()) {
                  comboBox.getSelectionModel().select(0);
               } else {
                  comboBox.getSelectionModel().clearSelection();
               }
            } else {
               int idx = isBlank(comboBox) ? selected[0] + 1 : selected[0];
               comboBox.getSelectionModel().select(idx);
            }
         } finally {
            updating = false;
         }
      }

      @Override
      public void updateList(List list) throws Exception {
         updating = true;
         try {
            int currentIdx = comboBox.getSelectionModel().getSelectedIndex();
            comboBox.getItems().clear();
            if (isBlank(comboBox)) {
               Object blankLabel = comboBox.getProperties().get(JavaFXBinder.BLANK_LABEL_PROPERTY);
               comboBox.getItems().add(blankLabel != null ? blankLabel : "");
            }
            if (list != null) {
               comboBox.getItems().addAll(list);
            }
            if (dataProviderMetadata.isResetSelection()) {
               if (isBlank(comboBox) && !comboBox.getItems().isEmpty()) {
                  comboBox.getSelectionModel().select(0);
               } else {
                  comboBox.getSelectionModel().clearSelection();
               }
            } else if (currentIdx >= 0 && currentIdx < comboBox.getItems().size()) {
               comboBox.getSelectionModel().select(currentIdx);
            }
         } finally {
            updating = false;
         }
      }

      @Override
      public void unbind() {
         comboBox.getSelectionModel().selectedIndexProperty().removeListener(listener);
      }
   }
}
