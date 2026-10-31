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
package dev.neongenesis.ui.javafx;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.javafx.components.ComboBoxNodeBinder;
import dev.neongenesis.ui.javafx.components.LabeledNodeBinder;
import dev.neongenesis.ui.javafx.components.ListViewNodeBinder;
import dev.neongenesis.ui.javafx.components.ProgressBarNodeBinder;
import dev.neongenesis.ui.javafx.components.SliderNodeBinder;
import dev.neongenesis.ui.javafx.components.TableViewNodeBinder;
import dev.neongenesis.ui.javafx.components.TextInputControlNodeBinder;
import dev.neongenesis.ui.javafx.components.ToggleButtonNodeBinder;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class JavaFXBinderTest extends GenesisTestCase {
   public JavaFXBinderTest() {
      super("JavaFX Binder Unit Test");
   }

   public void testNodeBinderRegistryLookups() {
      NodeBinderRegistry registry = NodeBinderRegistry.getInstance();
      assertTrue(registry.get(TextField.class, true) instanceof TextInputControlNodeBinder);
      assertTrue(registry.get(Label.class, true) instanceof LabeledNodeBinder);
      assertTrue(registry.get(CheckBox.class, true) instanceof ToggleButtonNodeBinder);
      assertTrue(registry.get(ComboBox.class, true) instanceof ComboBoxNodeBinder);
      assertTrue(registry.get(ListView.class, true) instanceof ListViewNodeBinder);
      assertTrue(registry.get(TableView.class, true) instanceof TableViewNodeBinder);
      assertTrue(registry.get(Slider.class, true) instanceof SliderNodeBinder);
      assertTrue(registry.get(ProgressBar.class, true) instanceof ProgressBarNodeBinder);
      assertNotNull(registry.get(Button.class, true));
      assertNotNull(registry.getToggleGroupBinder());
   }

   public void testBreadthFirstLookupAndBindLifecycle() {
      Pane root = new Pane();
      VBox container = new VBox();
      Group childNode = new Group();
      childNode.setId("stringField");
      container.getChildren().add(childNode);
      root.getChildren().add(container);

      MockForm form = new MockForm();
      JavaFXBinder binder = new JavaFXBinder(root, form, new Object());

      assertSame(childNode, binder.lookup("stringField"));
      assertFalse(Boolean.TRUE.equals(root.getProperties().get(AbstractBinder.GENESIS_BOUND)));

      binder.bind();
      assertEquals(Boolean.TRUE, root.getProperties().get(AbstractBinder.GENESIS_BOUND));

      binder.unbind();
      assertEquals(Boolean.FALSE, root.getProperties().get(AbstractBinder.GENESIS_BOUND));
   }

   public void testVirtualPropertyAndExplicitRegistration() {
      Pane root = new Pane();
      Group customNode = new Group();
      customNode.getProperties().put(AbstractBinder.VIRTUAL, Boolean.TRUE);

      JavaFXBinder binder = new JavaFXBinder(root, new MockForm(), new Object());
      binder.register("customWidget", customNode);

      assertSame(customNode, binder.lookup("customWidget"));
      assertEquals("customWidget", binder.getName(customNode));
      assertTrue(binder.isVirtual(customNode));
   }
}
