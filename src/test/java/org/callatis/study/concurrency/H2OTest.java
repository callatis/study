package org.callatis.study.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class H2OTest {

    private static final long THREAD_JOIN_TIMEOUT_MS = 2000L;

    private final String water;

    public H2OTest(String water) {
        this.water = water;
    }

    @Parameters(name = "water={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // From H2O.md examples.
            {"HOH"},
            {"OOHHHH"},
            // Additional orderings to verify barrier behavior.
            {"HHO"},
            {"OHH"},
            {"HHOOHH"}
        });
    }

    @Test
    public void testBuildsWaterInValidMoleculeGroups() throws InterruptedException {
        H2O h2o = new H2O();
        @SuppressWarnings("java:S1149")
        StringBuffer output = new StringBuffer();
        AtomicReference<Throwable> threadError = new AtomicReference<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < water.length(); i++) {
            char atom = water.charAt(i);
            if (atom == 'H') {
                Thread hThread = new Thread(
                    () -> runSafely(() -> h2o.hydrogen(() -> output.append('H')), threadError),
                    "hydrogen-" + i);
                threads.add(hThread);
            } else {
                Thread oThread = new Thread(
                    () -> runSafely(() -> h2o.oxygen(() -> output.append('O')), threadError),
                    "oxygen-" + i);
                threads.add(oThread);
            }
        }

        for (Thread thread : threads) {
            thread.start();
        }

        waitForCompletion(threads);

        if (threadError.get() != null) {
            throw new AssertionError("Worker failed", threadError.get());
        }

        assertIsValidWaterOutput(output.toString(), water.length());
    }

    private static void waitForCompletion(List<Thread> threads) throws InterruptedException {
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

    private static void assertIsValidWaterOutput(String output, int expectedLength) {
        assertEquals(expectedLength, output.length());
        assertEquals(0, output.length() % 3);

        for (int i = 0; i < output.length(); i += 3) {
            int hCount = 0;
            int oCount = 0;
            for (int j = i; j < i + 3; j++) {
                if (output.charAt(j) == 'H') {
                    hCount++;
                } else if (output.charAt(j) == 'O') {
                    oCount++;
                }
            }

            assertEquals(2, hCount);
            assertEquals(1, oCount);
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