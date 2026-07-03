package org.callatis.study.concurrency;

import java.util.ArrayList;
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

    private final String implementationType;
    private final String water;

    public H2OTest(String implementationType, String water) {
        this.implementationType = implementationType;
        this.water = water;
    }

    @Parameters(name = "{0}-water={1}")
    public static Collection<Object[]> parameters() {
        String[] implementations = new String[] {"H2OConditions", "H2OOriginal", "H2OSemaphores", "H2OSimple"};
        String[] waters = new String[] {
            // From H2O.md examples.
            "OOHHHH",
            // Additional orderings to verify barrier behavior.
            "HHO",
            "OHH",
            "HOHOHH",
            "OOHHOHHHH",
            "HHOOHH",
            "HHHHHHHHHHOHHOHHHHOOHHHOOOOHHOOHOHHHHHOOHOHHHOOOOOOHHHHHHHHH", 
            "HOH"
        };

        List<Object[]> params = new ArrayList<>();
        for (String implementation : implementations) {
            for (String sequence : waters) {
                params.add(new Object[] {implementation, sequence});
            }
        }

        return params;
    }

    @Test
    public void testBuildsWaterInValidMoleculeGroups() throws InterruptedException {
        AtomBinder h2o = createImplementation();
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

    private AtomBinder createImplementation() {
        if ("H2OConditions".equals(implementationType)) {
            H2OConditions h2o = new H2OConditions();
            return new AtomBinder() {
                @Override
                public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
                    h2o.hydrogen(releaseHydrogen);
                }

                @Override
                public void oxygen(Runnable releaseOxygen) throws InterruptedException {
                    h2o.oxygen(releaseOxygen);
                }
            };
        }

        if ("H2OOriginal".equals(implementationType)) {
            H2OOriginal h2o = new H2OOriginal();
            return new AtomBinder() {
                @Override
                public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
                    h2o.hydrogen(releaseHydrogen);
                }

                @Override
                public void oxygen(Runnable releaseOxygen) throws InterruptedException {
                    h2o.oxygen(releaseOxygen);
                }
            };
        }

        if ("H2OSimple".equals(implementationType)) {
            H2OSimple h2o = new H2OSimple();
            return new AtomBinder() {
                @Override
                public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
                    h2o.hydrogen(releaseHydrogen);
                }

                @Override
                public void oxygen(Runnable releaseOxygen) throws InterruptedException {
                    h2o.oxygen(releaseOxygen);
                }
            };
        }

        if ("H2OSemaphores".equals(implementationType)) {
            H2OSemaphores h2o = new H2OSemaphores();
            return new AtomBinder() {
                @Override
                public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
                    h2o.hydrogen(releaseHydrogen);
                }

                @Override
                public void oxygen(Runnable releaseOxygen) throws InterruptedException {
                    h2o.oxygen(releaseOxygen);
                }
            };
        }

        throw new IllegalArgumentException("Unsupported implementation type: " + implementationType);
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

    private interface AtomBinder {
        void hydrogen(Runnable releaseHydrogen) throws InterruptedException;

        void oxygen(Runnable releaseOxygen) throws InterruptedException;
    }
}