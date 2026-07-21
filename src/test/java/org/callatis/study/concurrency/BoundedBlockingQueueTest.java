package org.callatis.study.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BoundedBlockingQueueTest {

    private static final long BLOCK_CHECK_DELAY_MS = 150L;
    private static final long THREAD_JOIN_TIMEOUT_MS = 2000L;

    private final String implementationType;

    public BoundedBlockingQueueTest(String implementationType) {
        this.implementationType = implementationType;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            {"simple"},
            {"semaphores"},
            {"conditions"}
        });
    }

    @Test
    public void testExample1FromProblemStatement() throws InterruptedException {
        BlockingQueue queue = createQueue(2);

        queue.enqueue(1);
        assertEquals(1, queue.dequeue());

        AtomicReference<Integer> blockedDequeueResult = new AtomicReference<>();
        AtomicReference<Throwable> blockedDequeueError = new AtomicReference<>();
        Thread blockedDequeueThread = new Thread(
            () -> runSafely(() -> blockedDequeueResult.set(queue.dequeue()), blockedDequeueError),
            "blocked-dequeue-thread");

        blockedDequeueThread.start();
        Thread.sleep(BLOCK_CHECK_DELAY_MS);
        assertTrue("dequeue should block while queue is empty", blockedDequeueThread.isAlive());

        queue.enqueue(0);
        blockedDequeueThread.join(THREAD_JOIN_TIMEOUT_MS);
        assertNoThreadError("blocked dequeue", blockedDequeueError.get());
        assertEquals(Integer.valueOf(0), blockedDequeueResult.get());

        queue.enqueue(2);
        queue.enqueue(3);

        AtomicReference<Throwable> blockedEnqueueError = new AtomicReference<>();
        Thread blockedEnqueueThread = new Thread(
            () -> runSafely(() -> queue.enqueue(4), blockedEnqueueError),
            "blocked-enqueue-thread");

        blockedEnqueueThread.start();
        Thread.sleep(BLOCK_CHECK_DELAY_MS);
        assertTrue("enqueue should block while queue is full", blockedEnqueueThread.isAlive());

        assertEquals(2, queue.dequeue());

        blockedEnqueueThread.join(THREAD_JOIN_TIMEOUT_MS);
        assertNoThreadError("blocked enqueue", blockedEnqueueError.get());

        assertEquals(2, queue.size());
    }

    @Test
    public void testExample2FromProblemStatement() throws InterruptedException {
        BlockingQueue queue = createQueue(3);

        queue.enqueue(1);
        queue.enqueue(0);
        queue.enqueue(2);

        List<Integer> dequeued = new ArrayList<>();
        AtomicReference<Throwable> threadError = new AtomicReference<>();

        Thread c1 = new Thread(() -> runSafely(() -> addSynchronized(dequeued, queue.dequeue()), threadError), "consumer-1");
        Thread c2 = new Thread(() -> runSafely(() -> addSynchronized(dequeued, queue.dequeue()), threadError), "consumer-2");
        Thread c3 = new Thread(() -> runSafely(() -> addSynchronized(dequeued, queue.dequeue()), threadError), "consumer-3");

        c1.start();
        c2.start();
        c3.start();

        c1.join(THREAD_JOIN_TIMEOUT_MS);
        c2.join(THREAD_JOIN_TIMEOUT_MS);
        c3.join(THREAD_JOIN_TIMEOUT_MS);

        if (c1.isAlive() || c2.isAlive() || c3.isAlive()) {
            c1.interrupt();
            c2.interrupt();
            c3.interrupt();
            fail("Consumer threads did not complete in time. Potential deadlock.");
        }

        assertNoThreadError("example2 consumers", threadError.get());

        List<Integer> sorted = new ArrayList<>(dequeued);
        sorted.sort(Integer::compareTo);
        assertEquals(Arrays.asList(0, 1, 2), sorted);

        queue.enqueue(3);
        assertEquals(1, queue.size());
    }

    private static void addSynchronized(List<Integer> target, int value) {
        synchronized (target) {
            target.add(value);
        }
    }

    private static void runSafely(InterruptibleRunnable action, AtomicReference<Throwable> threadError) {
        try {
            action.run();
        } catch (Exception t) {
            threadError.compareAndSet(null, t);
        }
    }

    private static void assertNoThreadError(String label, Throwable error) {
        if (error != null) {
            throw new AssertionError("Worker failed for " + label, error);
        }
    }

    private BlockingQueue createQueue(int capacity) {
        if ("simple".equals(this.implementationType)) {
            return new BoundedBlockingQueueSimple(capacity);
        }

        if ("semaphores".equals(this.implementationType)) {
            return new BoundedBlockingQueueSemaphores(capacity);
        }

        if ("conditions".equals(this.implementationType)) {
            return new BoundedBlockingQueueConditions(capacity);
        }

        throw new IllegalArgumentException("Unsupported implementation: " + this.implementationType);
    }

    @FunctionalInterface
    private interface InterruptibleRunnable {
        void run() throws Exception;
    }
}
