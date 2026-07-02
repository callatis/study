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
public class AlternateFooBarTest {

    private static final long THREAD_JOIN_TIMEOUT_MS = 2000L;

    private final int n;
    private final String expectedOutput;

    public AlternateFooBarTest(int n, String expectedOutput) {
        this.n = n;
        this.expectedOutput = expectedOutput;
    }

    @Parameters(name = "Example {0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // From AlternateFooBar.md examples.
            {1, "foobar"},
            {2, "foobarfoobar"}
        });
    }

    @Test
    public void testPrintsFooBarAlternately() throws InterruptedException {
        AlternateFooBar fooBar = new AlternateFooBar(n);
        @SuppressWarnings("java:S1149")
        final StringBuffer output = new StringBuffer();
        AtomicReference<Throwable> threadError = new AtomicReference<>();

        Thread fooThread = new Thread(() -> runSafely(() -> fooBar.foo(() -> output.append("foo")), threadError), "foo-thread");
        Thread barThread = new Thread(() -> runSafely(() -> fooBar.bar(() -> output.append("bar")), threadError), "bar-thread");

        fooThread.start();
        barThread.start();

        waitForCompletion(fooThread, barThread);

        if (threadError.get() != null) {
            Throwable thr = threadError.get();
            throw new AssertionError("Worker failed", thr);
        }

        assertEquals(expectedOutput, output.toString());
    }

    private static void waitForCompletion(Thread fooThread, Thread barThread) throws InterruptedException {
        fooThread.join(THREAD_JOIN_TIMEOUT_MS);
        barThread.join(THREAD_JOIN_TIMEOUT_MS);

        if (fooThread.isAlive() || barThread.isAlive()) {
            fooThread.interrupt();
            barThread.interrupt();
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