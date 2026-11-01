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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import dev.neongenesis.GenesisTestCase;
import dev.neongenesis.mockobjects.MockForm;
import dev.neongenesis.ui.binding.AbstractBinder;
import javafx.application.Platform;
import dev.neongenesis.ui.javafx.components.ComboBoxNodeBinder;
import dev.neongenesis.ui.javafx.components.LabeledNodeBinder;
import dev.neongenesis.ui.javafx.components.ListViewNodeBinder;
import dev.neongenesis.ui.javafx.components.ProgressBarNodeBinder;
import dev.neongenesis.ui.javafx.components.SliderNodeBinder;
import dev.neongenesis.ui.javafx.components.TableViewNodeBinder;
import dev.neongenesis.ui.javafx.components.TextInputControlNodeBinder;
import dev.neongenesis.ui.javafx.components.ToggleButtonNodeBinder;
import javafx.scene.Group;
import javafx.scene.Node;
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
   private static boolean fxPlatformStarted;

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

   public void testLoadAndBindFXMLView() throws IOException {
      URL location = getClass().getResource("test-view.fxml");
      assertNotNull(location);

      MockForm form = new MockForm();
      FXMLView<TestViewController> view = FXMLView.load(location, form);
      try {
         assertTrue(view.getRoot() instanceof Node);
         assertNotNull(view.getController());
         assertSame(form, view.getBinder().getForm());
         assertEquals(Boolean.TRUE, view.getRoot().getProperties()
               .get(AbstractBinder.GENESIS_BOUND));
      } finally {
         view.close();
      }

      assertEquals(Boolean.FALSE, view.getRoot().getProperties()
            .get(AbstractBinder.GENESIS_BOUND));
   }

   public void testControllerFactoryIsUsedWhenLoadingFXML() throws IOException {
      URL location = getClass().getResource("test-view.fxml");
      TestViewController controller = new TestViewController();

      FXMLView<TestViewController> view = FXMLView.load(location,
            new MockForm(), type -> controller);
      try {
         assertSame(controller, view.getController());
      } finally {
         view.close();
      }
   }

   public void testFXMLWithoutControllerIsRejected() throws IOException {
      URL location = getClass().getResource("view-without-controller.fxml");
      assertNotNull(location);

      try {
         FXMLView.load(location, new MockForm());
         fail("FXML documents without a controller should be rejected");
      } catch (IOException expected) {
         assertTrue(expected.getMessage().contains("controller"));
      }
   }

   public void testAsyncWorkUsesVirtualThreadAndCompletesOnFxThread()
         throws InterruptedException {
      ensureFxPlatformStarted();

      JavaFXBinder binder = new JavaFXBinder(new Pane(), new MockForm(),
            new Object());
      binder.bind();

      CountDownLatch started = new CountDownLatch(1);
      CountDownLatch continueTask = new CountDownLatch(1);
      CountDownLatch completed = new CountDownLatch(1);
      AtomicReference<Boolean> completedOnFxThread = new AtomicReference<>(false);
      CompletableFuture<Boolean> task = binder.runAsync(() -> {
         started.countDown();
         continueTask.await();
         return Thread.currentThread().isVirtual();
      });
      try {
         assertTrue("Async task did not start", started.await(10,
               TimeUnit.SECONDS));
         task.whenComplete((usedVirtualThread, failure) -> {
            completedOnFxThread.set(failure == null && usedVirtualThread
                  && Platform.isFxApplicationThread());
            completed.countDown();
         });
         continueTask.countDown();

         assertTrue("Async task did not complete", completed.await(10,
               TimeUnit.SECONDS));
         assertTrue("Task completion must run on the FX thread",
               completedOnFxThread.get());
      } finally {
         continueTask.countDown();
         binder.unbind();
      }
   }

   public void testAsyncFailureIsCompletedOnFxThread()
         throws InterruptedException {
      ensureFxPlatformStarted();

      JavaFXBinder binder = new JavaFXBinder(new Pane(), new MockForm(),
            new Object());
      binder.bind();

      RuntimeException expectedFailure = new RuntimeException("expected");
      CountDownLatch started = new CountDownLatch(1);
      CountDownLatch continueTask = new CountDownLatch(1);
      CountDownLatch completed = new CountDownLatch(1);
      AtomicReference<Throwable> actualFailure = new AtomicReference<>();
      AtomicReference<Boolean> completedOnFxThread = new AtomicReference<>(false);
      binder.runAsync(() -> {
         started.countDown();
         continueTask.await();
         throw expectedFailure;
      }).whenComplete((result, failure) -> {
         actualFailure.set(failure);
         completedOnFxThread.set(Platform.isFxApplicationThread());
         completed.countDown();
      });
      try {
         assertTrue("Async task did not start", started.await(10,
               TimeUnit.SECONDS));
         continueTask.countDown();

         assertTrue("Failed task did not complete", completed.await(10,
               TimeUnit.SECONDS));
         assertSame(expectedFailure, actualFailure.get());
         assertTrue("Failure completion must run on the FX thread",
               completedOnFxThread.get());
      } finally {
         continueTask.countDown();
         binder.unbind();
      }
   }

   public void testUnbindCancelsAsyncWork() throws InterruptedException {
      ensureFxPlatformStarted();

      JavaFXBinder binder = new JavaFXBinder(new Pane(), new MockForm(),
            new Object());
      binder.bind();

      CountDownLatch started = new CountDownLatch(1);
      CountDownLatch interrupted = new CountDownLatch(1);
      CompletableFuture<Void> task = binder.runAsync(() -> {
         started.countDown();
         try {
            new CountDownLatch(1).await();
         } catch (InterruptedException expected) {
            interrupted.countDown();
         }
         return null;
      });

      assertTrue("Async task did not start", started.await(10,
            TimeUnit.SECONDS));
      binder.unbind();

      assertTrue("Unbinding must cancel outstanding task", task.isCancelled());
      assertTrue("Unbinding must interrupt the worker",
            interrupted.await(10, TimeUnit.SECONDS));
   }

   public void testCancellingFutureInterruptsAsyncWork()
         throws InterruptedException {
      ensureFxPlatformStarted();

      JavaFXBinder binder = new JavaFXBinder(new Pane(), new MockForm(),
            new Object());
      binder.bind();

      CountDownLatch started = new CountDownLatch(1);
      CountDownLatch interrupted = new CountDownLatch(1);
      CompletableFuture<Void> task = binder.runAsync(() -> {
         started.countDown();
         try {
            new CountDownLatch(1).await();
         } catch (InterruptedException expected) {
            interrupted.countDown();
         }
         return null;
      });

      try {
         assertTrue("Async task did not start", started.await(10,
               TimeUnit.SECONDS));
         assertTrue("Cancelling the future must succeed", task.cancel(true));
         assertTrue("Future cancellation must interrupt the worker",
               interrupted.await(10, TimeUnit.SECONDS));
      } finally {
         binder.unbind();
      }
   }

   public void testAsyncBatchRunsInParallelAndPreservesInputOrder()
         throws InterruptedException {
      ensureFxPlatformStarted();

      JavaFXBinder binder = new JavaFXBinder(new Pane(), new MockForm(),
            new Object());
      binder.bind();

      CountDownLatch firstWaveStarted = new CountDownLatch(2);
      CountDownLatch releaseFirstWave = new CountDownLatch(1);
      CountDownLatch completed = new CountDownLatch(1);
      AtomicInteger active = new AtomicInteger();
      AtomicInteger maximumActive = new AtomicInteger();
      AtomicReference<Boolean> completedOnFxThread = new AtomicReference<>(false);
      AtomicReference<java.util.List<Integer>> actualResults =
            new AtomicReference<>();
      CompletableFuture<java.util.List<Integer>> batch = binder.runAsyncBatch(
            Arrays.asList(
                  () -> {
                     recordMaximum(maximumActive, active.incrementAndGet());
                     firstWaveStarted.countDown();
                     try {
                        releaseFirstWave.await();
                     } finally {
                        active.decrementAndGet();
                     }
                     return 1;
                  },
                  () -> {
                     recordMaximum(maximumActive, active.incrementAndGet());
                     firstWaveStarted.countDown();
                     try {
                        releaseFirstWave.await();
                     } finally {
                        active.decrementAndGet();
                     }
                     return 2;
                  },
                  () -> 3),
            2);
      batch.whenComplete((results, failure) -> {
         actualResults.set(results);
         completedOnFxThread.set(failure == null
               && Platform.isFxApplicationThread());
         completed.countDown();
      });

      try {
         assertTrue("Parallel operations did not start",
               firstWaveStarted.await(10, TimeUnit.SECONDS));
         assertEquals("Concurrency must honor the configured maximum", 2,
               maximumActive.get());
         releaseFirstWave.countDown();

         assertTrue("Batch did not complete", completed.await(10,
               TimeUnit.SECONDS));
         assertEquals(Arrays.asList(1, 2, 3), actualResults.get());
         assertTrue("Batch completion must run on the FX thread",
               completedOnFxThread.get());
      } finally {
         releaseFirstWave.countDown();
         binder.unbind();
      }
   }

   public void testAsyncBatchFailureCancelsSiblingsAndStopsScheduling()
         throws InterruptedException {
      ensureFxPlatformStarted();

      JavaFXBinder binder = new JavaFXBinder(new Pane(), new MockForm(),
            new Object());
      binder.bind();

      RuntimeException expectedFailure = new RuntimeException("batch failure");
      CountDownLatch firstWaveStarted = new CountDownLatch(2);
      CountDownLatch releaseFailingTask = new CountDownLatch(1);
      CountDownLatch siblingInterrupted = new CountDownLatch(1);
      CountDownLatch completed = new CountDownLatch(1);
      AtomicInteger thirdOperationCalls = new AtomicInteger();
      AtomicReference<Throwable> actualFailure = new AtomicReference<>();
      AtomicReference<Boolean> completedOnFxThread = new AtomicReference<>(false);
      CompletableFuture<java.util.List<Integer>> batch = binder.runAsyncBatch(
            Arrays.asList(
                  () -> {
                     firstWaveStarted.countDown();
                     releaseFailingTask.await();
                     throw expectedFailure;
                  },
                  () -> {
                     firstWaveStarted.countDown();
                     try {
                        new CountDownLatch(1).await();
                     } catch (InterruptedException expected) {
                        siblingInterrupted.countDown();
                     }
                     return -1;
                  },
                  () -> {
                     thirdOperationCalls.incrementAndGet();
                     return 3;
                  }),
            2);
      batch.whenComplete((results, failure) -> {
         actualFailure.set(failure);
         completedOnFxThread.set(Platform.isFxApplicationThread());
         completed.countDown();
      });

      try {
         assertTrue("Parallel operations did not start",
               firstWaveStarted.await(10, TimeUnit.SECONDS));
         releaseFailingTask.countDown();

         assertTrue("Failed batch did not complete", completed.await(10,
               TimeUnit.SECONDS));
         assertSame(expectedFailure, actualFailure.get());
         assertTrue("Failure completion must run on the FX thread",
               completedOnFxThread.get());
         assertTrue("Failure must interrupt in-flight sibling work",
               siblingInterrupted.await(10, TimeUnit.SECONDS));
         assertEquals("Failure must prevent later operations from starting",
               0, thirdOperationCalls.get());
      } finally {
         releaseFailingTask.countDown();
         binder.unbind();
      }
   }

   public void testUnbindCancelsAsyncBatch() throws InterruptedException {
      ensureFxPlatformStarted();

      JavaFXBinder binder = new JavaFXBinder(new Pane(), new MockForm(),
            new Object());
      binder.bind();

      CountDownLatch started = new CountDownLatch(1);
      CountDownLatch interrupted = new CountDownLatch(1);
      CompletableFuture<java.util.List<Void>> batch = binder.runAsyncBatch(
            Arrays.asList(() -> {
               started.countDown();
               try {
                  new CountDownLatch(1).await();
               } catch (InterruptedException expected) {
                  interrupted.countDown();
               }
               return null;
            }));

      assertTrue("Batch operation did not start", started.await(10,
            TimeUnit.SECONDS));
      binder.unbind();

      assertTrue("Unbinding must cancel the batch", batch.isCancelled());
      assertTrue("Unbinding must interrupt batch workers",
            interrupted.await(10, TimeUnit.SECONDS));
   }

   private static void recordMaximum(AtomicInteger maximum, int value) {
      maximum.accumulateAndGet(value, Math::max);
   }

   private static void ensureFxPlatformStarted() throws InterruptedException {
      synchronized (JavaFXBinderTest.class) {
         if (fxPlatformStarted) {
            return;
         }

         CountDownLatch started = new CountDownLatch(1);
         try {
            Platform.startup(() -> {
               Platform.setImplicitExit(false);
               started.countDown();
            });
         } catch (IllegalStateException alreadyStarted) {
            Platform.runLater(() -> {
               Platform.setImplicitExit(false);
               started.countDown();
            });
         }

         if (!started.await(10, TimeUnit.SECONDS)) {
            throw new AssertionError("JavaFX platform did not start");
         }
         fxPlatformStarted = true;
      }
   }

   public static class TestViewController {
   }
}
