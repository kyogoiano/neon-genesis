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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import dev.neongenesis.ui.binding.AbstractBinder;
import dev.neongenesis.ui.binding.ExceptionHandler;
import dev.neongenesis.ui.binding.GroupBinder;
import dev.neongenesis.ui.binding.LookupStrategy;
import dev.neongenesis.ui.binding.WidgetBinder;
import dev.neongenesis.ui.javafx.lookup.BreadthFirstNodeLookupStrategy;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ToggleGroup;

/**
 * {@code JavaFXBinder} is the default implementation of {@link AbstractBinder}
 * for OpenJFX 21+. It connects a JavaFX {@link Node} hierarchy (such as a
 * {@code Pane}, {@code VBox}, {@code GridPane}, or FXML root) to a Neon Genesis
 * {@code @Form} POJO and {@code @ViewHandler}.
 */
public class JavaFXBinder extends AbstractBinder {
   public static final String BINDER_KEY = "genesis:JavaFXBinder"; // NOI18N
   public static final String BUTTON_GROUP_SELECTION_VALUE_KEY = "genesis:buttonGroupSelectionValue"; // NOI18N
   private static final int DEFAULT_ASYNC_BATCH_PARALLELISM =
         Math.max(1, Runtime.getRuntime().availableProcessors());

   private final NodeBinderRegistry factory = NodeBinderRegistry.getInstance();
   private final Object asyncTaskLock = new Object();
   private final Set<AsyncTask<?>> asyncTasks = new HashSet<>();
   private final Set<CompletableFuture<?>> asyncBatches = new HashSet<>();
   private boolean acceptsAsyncTasks;
   private long asyncTaskSequence;

   public JavaFXBinder(Node root, Object form) {
      this(root, null, form, root);
   }

   public JavaFXBinder(Node root, LookupStrategy lookupStrategy, Object form) {
      this(root, lookupStrategy, form, root);
   }

   public JavaFXBinder(Node root, Object form, Object handler) {
      this(root, null, form, handler);
   }

   public JavaFXBinder(Node root, LookupStrategy lookupStrategy, Object form, Object handler) {
      super(root, form, handler, lookupStrategy);
   }

   @Override
   protected ExceptionHandler createExceptionHandler() {
      return new JavaFXExceptionHandler((Node) getRoot());
   }

   @Override
   protected LookupStrategy createLookupStrategy() {
      return new BreadthFirstNodeLookupStrategy();
   }

   public Node register(String name, Node node) {
      return (Node) getLookupStrategy().register(name, node);
   }

   public ToggleGroup registerToggleGroup(String name, ToggleGroup toggleGroup) {
      return (ToggleGroup) registerGroup(name, toggleGroup);
   }

   public ToggleGroup registerToggleGroup(String name, ToggleGroup toggleGroup, GroupBinder groupBinder) {
      return (ToggleGroup) registerGroup(name, toggleGroup, groupBinder);
   }

   /**
    * Runs blocking work on a virtual thread and completes the returned future
    * on the JavaFX Application Thread. The binder must be bound before calling
    * this method.
    *
    * <p>Failures are reported as exceptional completion. Cancelling the
    * returned future interrupts the worker thread; the operation must respond
    * to interruption for cancellation to stop its work. Unbinding this binder
    * cancels all tasks it started. Completion is posted to the JavaFX
    * Application Thread; dependent actions registered before completion run
    * there. As with any {@link CompletableFuture}, actions registered after it
    * has completed run on the registering thread. No implicit timeout is
    * applied; use timeouts appropriate to the operation being performed.</p>
    *
    * @param operation the blocking work to run away from the JavaFX thread
    * @param <T> the result type
    * @return a future completed on the JavaFX Application Thread
    */
   public <T> CompletableFuture<T> runAsync(Callable<T> operation) {
      Objects.requireNonNull(operation, "operation");

      synchronized (asyncTaskLock) {
         if (!acceptsAsyncTasks) {
            throw new IllegalStateException(
                  "JavaFXBinder must be bound before running asynchronous work");
         }

         AsyncTask<T> task = new AsyncTask<>(operation);
         task.result.whenComplete((result, failure) -> {
            if (task.result.isCancelled()) {
               task.interruptWorker();
            }
            removeAsyncTask(task);
         });
         asyncTasks.add(task);

         Thread worker = Thread.ofVirtual()
               .name("neon-genesis-javafx-task-" + ++asyncTaskSequence)
               .unstarted(task);
         task.setWorker(worker);
         try {
            worker.start();
         } catch (RuntimeException | Error failure) {
            asyncTasks.remove(task);
            throw failure;
         }

         return task.result;
      }
   }

   /**
    * Runs a batch of blocking operations in parallel on virtual threads,
    * limiting concurrency to the number of available processors. Results are
    * returned in input order, regardless of completion order.
    *
    * <p>If an operation fails, the batch completes exceptionally and the
    * remaining in-flight operations are cancelled. Cancelling the returned
    * future interrupts in-flight workers; operations must cooperate with
    * interruption. Unbinding this binder cancels the batch.</p>
    *
    * @param operations the blocking operations to run
    * @param <T> the result type
    * @return a future completed on the JavaFX Application Thread
    */
   public <T> CompletableFuture<List<T>> runAsyncBatch(
         List<? extends Callable<? extends T>> operations) {
      return runAsyncBatch(operations, DEFAULT_ASYNC_BATCH_PARALLELISM);
   }

   /**
    * Runs a batch of blocking operations in parallel on virtual threads,
    * limiting the number of concurrently running operations. Results are
    * returned in input order, regardless of completion order.
    *
    * @param operations the blocking operations to run
    * @param maxParallelism maximum number of operations to run concurrently
    * @param <T> the result type
    * @return a future completed on the JavaFX Application Thread
    */
   public <T> CompletableFuture<List<T>> runAsyncBatch(
         List<? extends Callable<? extends T>> operations,
         int maxParallelism) {
      Objects.requireNonNull(operations, "operations");
      if (maxParallelism < 1) {
         throw new IllegalArgumentException(
               "maxParallelism must be greater than zero");
      }

      List<Callable<? extends T>> operationSnapshot =
            new ArrayList<>(operations.size());
      for (Callable<? extends T> operation : operations) {
         operationSnapshot.add(Objects.requireNonNull(operation, "operation"));
      }

      AsyncBatch<T> batch = new AsyncBatch<>(operationSnapshot, maxParallelism);
      synchronized (asyncTaskLock) {
         if (!acceptsAsyncTasks) {
            throw new IllegalStateException(
                  "JavaFXBinder must be bound before running asynchronous work");
         }
         asyncBatches.add(batch.result);
      }
      batch.result.whenComplete((result, failure) -> {
         synchronized (asyncTaskLock) {
            asyncBatches.remove(batch.result);
         }
         if (batch.result.isCancelled()) {
            batch.cancelRunning();
         }
      });

      batch.start();
      return batch.result;
   }

   private void removeAsyncTask(AsyncTask<?> task) {
      synchronized (asyncTaskLock) {
         asyncTasks.remove(task);
      }
   }

   private void cancelAsyncTasks() {
      List<AsyncTask<?>> tasks;
      List<CompletableFuture<?>> batches;
      synchronized (asyncTaskLock) {
         acceptsAsyncTasks = false;
         tasks = new ArrayList<>(asyncTasks);
         batches = new ArrayList<>(asyncBatches);
      }

      for (CompletableFuture<?> batch : batches) {
         batch.cancel(true);
      }

      for (AsyncTask<?> task : tasks) {
         task.cancel();
      }
   }

   @Override
   public void unbind() {
      cancelAsyncTasks();
      super.unbind();
   }

   @Override
   public boolean isVirtual(Object widget) {
      return widget instanceof Node node && Boolean.TRUE.equals(node.getProperties().get(VIRTUAL));
   }

   @Override
   protected WidgetBinder getDefaultWidgetBinderFor(Object object) {
      return factory.get(object.getClass(), true);
   }

   @Override
   protected GroupBinder getDefaultGroupBinderFor(Object group) {
      return factory.getToggleGroupBinder();
   }

   @Override
   protected void markBound() {
      synchronized (asyncTaskLock) {
         acceptsAsyncTasks = true;
      }
      if (getRoot() instanceof Node node) {
         node.getProperties().put(GENESIS_BOUND, Boolean.TRUE);
      }
   }

   @Override
   protected void markUnbound() {
      if (getRoot() instanceof Node node) {
         node.getProperties().put(GENESIS_BOUND, Boolean.FALSE);
      }
   }

   private final class AsyncBatch<T> {
      private final List<? extends Callable<? extends T>> operations;
      private final int maxParallelism;
      private final Object lock = new Object();
      private final List<T> results;
      private final Set<CompletableFuture<T>> running = new HashSet<>();
      private final CompletableFuture<List<T>> result = new CompletableFuture<>();
      private int nextOperation;
      private int completedCount;

      private AsyncBatch(
            List<? extends Callable<? extends T>> operations,
            int maxParallelism) {
         this.operations = operations;
         this.maxParallelism = maxParallelism;
         results = new ArrayList<>(operations.size());
         for (int i = 0; i < operations.size(); i++) {
            results.add(null);
         }
      }

      private void start() {
         if (operations.isEmpty()) {
            dispatchToFxThread(() -> result.complete(new ArrayList<>(results)));
            return;
         }

         for (int i = 0; i < Math.min(maxParallelism, operations.size()); i++) {
            launchNext();
         }
      }

      private void launchNext() {
         final int index;
         synchronized (lock) {
            if (result.isDone() || nextOperation == operations.size()) {
               return;
            }
            index = nextOperation++;
         }

         final CompletableFuture<T> task;
         try {
            task = JavaFXBinder.this.runAsync(
                  () -> operations.get(index).call());
         } catch (RuntimeException | Error failure) {
            dispatchToFxThread(() -> operationCompleted(index, null, null,
                  failure));
            return;
         }

         synchronized (lock) {
            if (result.isDone()) {
               task.cancel(true);
            } else {
               running.add(task);
            }
         }

         task.whenComplete((value, failure) -> dispatchToFxThread(
               () -> operationCompleted(index, task, value, failure)));
      }

      private void operationCompleted(int index, CompletableFuture<T> task,
            T value, Throwable failure) {
         boolean startNext = false;
         boolean complete = false;
         List<T> completedResults = null;
         synchronized (lock) {
            if (task != null) {
               running.remove(task);
            }

            if (result.isDone()) {
               return;
            }

            if (failure == null) {
               results.set(index, value);
               completedCount++;
               if (completedCount == operations.size()) {
                  complete = true;
                  completedResults = new ArrayList<>(results);
               } else {
                  startNext = true;
               }
            }
         }

         if (failure != null) {
            if (result.completeExceptionally(failure)) {
               cancelRunning();
            }
         } else if (complete) {
            result.complete(completedResults);
         } else if (startNext) {
            launchNext();
         }
      }

      private void dispatchToFxThread(Runnable action) {
         if (Platform.isFxApplicationThread()) {
            action.run();
            return;
         }

         try {
            Platform.runLater(action);
         } catch (IllegalStateException dispatchFailure) {
            result.completeExceptionally(dispatchFailure);
         }
      }

      private void cancelRunning() {
         List<CompletableFuture<T>> tasks;
         synchronized (lock) {
            tasks = new ArrayList<>(running);
         }

         for (CompletableFuture<T> task : tasks) {
            task.cancel(true);
         }
      }
   }

   private final class AsyncTask<T> implements Runnable {
      private final Callable<T> operation;
      private final CompletableFuture<T> result = new CompletableFuture<>();
      private volatile Thread worker;

      private AsyncTask(Callable<T> operation) {
         this.operation = operation;
      }

      private void setWorker(Thread worker) {
         this.worker = worker;
      }

      @Override
      public void run() {
         try {
            T value = operation.call();
            completeOnFxThread(value, null);
         } catch (Throwable failure) {
            completeOnFxThread(null, failure);
         }
      }

      private void completeOnFxThread(T value, Throwable failure) {
         if (result.isDone()) {
            return;
         }

         try {
            Platform.runLater(() -> {
               if (failure == null) {
                  result.complete(value);
               } else {
                  result.completeExceptionally(failure);
               }
            });
         } catch (IllegalStateException dispatchFailure) {
            if (failure != null) {
               dispatchFailure.addSuppressed(failure);
            }
            result.completeExceptionally(dispatchFailure);
         }
      }

      private void cancel() {
         result.cancel(true);
      }

      private void interruptWorker() {
         Thread currentWorker = worker;
         if (currentWorker != null) {
            currentWorker.interrupt();
         }
      }
   }
}
