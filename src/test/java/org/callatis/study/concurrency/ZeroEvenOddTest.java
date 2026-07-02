package org.callatis.study.concurrency;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ZeroEvenOddTest {

    private static final long THREAD_JOIN_TIMEOUT_MS = 2000L;

    private final int n;
    private final String expectedOutput;

    public ZeroEvenOddTest(int n, String expectedOutput) {
        this.n = n;
        this.expectedOutput = expectedOutput;
    }

    @Parameters(name = "Example {0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // From ZeroEvenOdd.md examples.
            {2, "0102"},
            {5, "0102030405"}
        });
    }

    @Test
    public void testZeroEvenOdd() throws InterruptedException {
        ZeroEvenOdd zeroEvenOdd = new ZeroEvenOdd(n);
        @SuppressWarnings("java:S1149")
        final StringBuffer output = new StringBuffer();
        AtomicReference<Throwable> threadError = new AtomicReference<>();

        IntConsumer printNumber = value -> output.append(value);

        Thread zeroThread = new Thread(() -> runSafely(() -> zeroEvenOdd.zero(printNumber), threadError), "zero-thread");
        Thread evenThread = new Thread(() -> runSafely(() -> zeroEvenOdd.even(printNumber), threadError), "even-thread");
        Thread oddThread = new Thread(() -> runSafely(() -> zeroEvenOdd.odd(printNumber), threadError), "odd-thread");

        zeroThread.start();
        evenThread.start();
        oddThread.start();

        waitForCompletion(zeroThread, evenThread, oddThread);

        if (threadError.get() != null) {
            Throwable thr = threadError.get();
            throw new AssertionError("Worker failed", thr);
        }

        assertEquals(expectedOutput, output.toString());
    }

    private static void waitForCompletion(Thread zeroThread, Thread evenThread, Thread oddThread) throws InterruptedException {
        zeroThread.join(THREAD_JOIN_TIMEOUT_MS);
        evenThread.join(THREAD_JOIN_TIMEOUT_MS);
        oddThread.join(THREAD_JOIN_TIMEOUT_MS);

        if (zeroThread.isAlive() || evenThread.isAlive() || oddThread.isAlive()) {
            zeroThread.interrupt();
            evenThread.interrupt();
            oddThread.interrupt();
            fail("Threads did not complete in time. Potential deadlock.");
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
