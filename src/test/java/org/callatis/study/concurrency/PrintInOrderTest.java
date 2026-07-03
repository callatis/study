package org.callatis.study.concurrency;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PrintInOrderTest {

    private static final long THREAD_JOIN_TIMEOUT_MS = 2000L;

    private final int[] callOrder;
    private final String expectedOutput;

    public PrintInOrderTest(int[] callOrder, String expectedOutput) {
        this.callOrder = callOrder;
        this.expectedOutput = expectedOutput;
    }

    @Parameters(name = "order={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // From PrintInOrder.md examples.
            {new int[] {1, 2, 3}, "firstsecondthird"},
            {new int[] {1, 3, 2}, "firstsecondthird"}
        });
    }

    @Test
    public void testPrintsInOrderRegardlessOfThreadSchedule() throws InterruptedException {
        PrintInOrder printer = new PrintInOrder();
        @SuppressWarnings("java:S1149")
        final StringBuffer output = new StringBuffer();
        AtomicReference<Throwable> threadError = new AtomicReference<>();

        Thread[] threads = new Thread[callOrder.length];

        for (int i = 0; i < callOrder.length; i++) {
            final int methodId = callOrder[i];
            threads[i] = createWorker(printer, output, threadError, methodId, i);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        waitForCompletion(threads);

        if (threadError.get() != null) {
            throw new AssertionError("Worker failed", threadError.get());
        }

        assertEquals(expectedOutput, output.toString());
    }

    private static Thread createWorker(
        PrintInOrder printer,
        StringBuffer output,
        AtomicReference<Throwable> threadError,
        int methodId,
        int index) {

        if (methodId == 1) {
            return new Thread(
                () -> runSafely(() -> printer.first(() -> output.append("first")), threadError),
                "first-thread-" + index);
        }

        if (methodId == 2) {
            return new Thread(
                () -> runSafely(() -> printer.second(() -> output.append("second")), threadError),
                "second-thread-" + index);
        }

        if (methodId == 3) {
            return new Thread(
                () -> runSafely(() -> printer.third(() -> output.append("third")), threadError),
                "third-thread-" + index);
        }

        throw new IllegalArgumentException("Unsupported method id: " + methodId);
    }

    private static void waitForCompletion(Thread[] threads) throws InterruptedException {
        for (Thread thread : threads) {
            thread.join(THREAD_JOIN_TIMEOUT_MS);
        }

        for (Thread thread : threads) {
            if (thread.isAlive()) {
                for (Thread t : threads) {
                    t.interrupt();
                }
                fail("Threads did not complete in time. Potential deadlock.");
            }
        }
    }

    private static void runSafely(ThrowingRunnable action, AtomicReference<Throwable> threadError) {
        try {
            action.run();
        } catch (InterruptedException t) {
            threadError.compareAndSet(null, t);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws InterruptedException;
    }
}