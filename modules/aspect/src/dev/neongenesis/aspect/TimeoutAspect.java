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
package dev.neongenesis.aspect;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import dev.neongenesis.exception.TimeoutException;

import dev.neongenesis.util.Bundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TimeoutAspect {
   private static final Log log = LogFactory.getLog(TimeoutAspect.class);

   private static final class LiveThreadWorkerThreadPair 
         extends PhantomReference {
      private final WeakReference reference;

      public LiveThreadWorkerThreadPair(Thread parentThread, ReferenceQueue queue,
            WorkerThread workerThread) {
         super(parentThread, queue);
         reference = new WeakReference(workerThread);
      }

      public WorkerThread getWorkerThread() {
         return (WorkerThread)reference.get();
      }
   }

   private static final class WorkerThreadDisposer implements Runnable {
      private static WorkerThreadDisposer instance;
      private final ReferenceQueue queue = new ReferenceQueue();
      private final Collection references = new LinkedList();

      public WorkerThreadDisposer() {
         Thread.ofVirtual().name("WorkerThreadDisposer-Daemon").start(this);
      }

      public static WorkerThreadDisposer getInstance() {
         synchronized (WorkerThreadDisposer.class) {
            if (instance == null) {
               instance = new WorkerThreadDisposer();
            }
         }

         return instance;
      }

      public void enqueue(Thread parentThread, WorkerThread workerThread) {
         LiveThreadWorkerThreadPair pair = new LiveThreadWorkerThreadPair(
               parentThread, queue, workerThread);

         synchronized (references) {
            references.add(pair);
         }
      }

      public void run() {
         while (true) {
            try {
               LiveThreadWorkerThreadPair pair = 
                     (LiveThreadWorkerThreadPair)queue.remove();
               WorkerThread worker = pair.getWorkerThread();

               if (worker != null) {
                  worker.interrupt();
               }
               
               pair.clear();

               synchronized (references) {
                  references.remove(pair);
               }
            } catch (InterruptedException ie) {
               log.error(Bundle.getMessage(TimeoutAspect.class,
                     "WORKERTHREADDISPOSED_INTERRUPTED"), ie); // NOI18N
               return;
            }
         }
      }
   }

   private static final class WorkerThread implements Runnable {
      private final boolean keepThreadInstance;
      private final Thread thread;
      private Throwable throwable;
      private Object returnValue;
      private ProceedingJoinPoint jp;
      private Object lock = new Object();
      private boolean started;
      private boolean running;
      private boolean waiting;

      public WorkerThread(boolean keepThreadInstance) {
         thread = Thread.ofVirtual()
               .name("WorkerThread-" + Thread.currentThread().getName())
               .unstarted(this);
         this.keepThreadInstance = keepThreadInstance;
      }

      public void execute(ProceedingJoinPoint jp, long timeout) {
         this.jp = jp;
         returnValue = null;
         throwable = null;
         running = true;
         waiting = false;

         if (!started) {
            started = true;
            thread.start();
         } else {
            synchronized (lock) {
               lock.notifyAll();
            }
         }

         synchronized (lock) {
            try {
               waiting = true;
               lock.wait(timeout);
            } catch (InterruptedException ie) {
               log.info(ie);
            }
         }
      }

      public void run() {
         do {
            try {
               returnValue = jp.proceed();
            } catch (final Throwable t) {
               throwable = t;
            }

            running = false;
            jp = null;

            while (!waiting) {
               try {
                  Thread.sleep(10);
               } catch (InterruptedException ie) {
                  if (log.isTraceEnabled()) {
                     log.trace(ie);
                  }

                  return;
               }
            }

            synchronized (lock) {
               lock.notifyAll();

               if (!keepThreadInstance) {
                  return;
               }

               try {
                  lock.wait();
               } catch (InterruptedException ie) {
                  if (log.isTraceEnabled()) {
                     log.trace(ie);
                  }

                  return;
               }
            }
         } while (true);
      }

      public Object getReturnValue() {
         return returnValue;
      }

      public Throwable getThrowable() {
         return throwable;
      }

      public void interrupt() {
         thread.interrupt();
      }

      public boolean isRunning() {
         return running;
      }

      public String toString() {
         return thread.toString();
      }

      public void cleanUp() {
         returnValue = null;
         throwable = null;
      }
   }

   private final long timeout;
   private final boolean keepThreadInstance;
   private final ThreadLocal<WorkerThread> threadLocal =
         new ThreadLocal<WorkerThread>();
   
   public TimeoutAspect() {
      this.timeout = Long.parseLong(System.getProperty(
            "genesis.aspect.timeout", "2500"));
      this.keepThreadInstance = Boolean.parseBoolean(System.getProperty(
            "genesis.aspect.keepThreadInstance", "true"));
   }

   private WorkerThread getWorkerThread() {
      return threadLocal.get();
   }

   private void setWorkerThread(WorkerThread workerThread) {
      threadLocal.set(workerThread);
   }

   @Around("execution(* dev.neongenesis.tests..TimeoutTest.*.*(..))")
   public Object timeoutAdvice(final ProceedingJoinPoint jp) throws Throwable {
      WorkerThread thread = getWorkerThread();

      if (thread == null) {
         thread = new WorkerThread(keepThreadInstance);
         setWorkerThread(thread);
  
         if (keepThreadInstance) {
            if (log.isDebugEnabled()) {
               log.debug(Bundle.getMessage(TimeoutAspect.class,
                     "NEW_THREAD_CREATED_X", thread)); // NOI18N
            }

            WorkerThreadDisposer.getInstance().enqueue(Thread.currentThread(), 
                  thread);
         }
      } else {
         if (keepThreadInstance && log.isDebugEnabled()) {
            log.debug(Bundle.getMessage(TimeoutAspect.class, "REUSING_THREAD_X", // NOI18N
                  thread));
         }
      }

      thread.execute(jp, timeout);

      if (thread.isRunning()) {
         try {
            thread.interrupt();
         } catch (Throwable t) {
            log.error(t);
         }

         setWorkerThread(null);

         throw new TimeoutException(Bundle.getMessage(TimeoutAspect.class,
               "EXECUTION_TOOK_MORE_THAN_X_MS", timeout)); // NOI18N
      }
 
      final Throwable throwable = thread.getThrowable();
      final Object returnValue = thread.getReturnValue();

      thread.cleanUp();

      if (!keepThreadInstance) {
         setWorkerThread(null);
      }

      if (throwable != null) {
         throw throwable;
      }

      return returnValue;
   }
}