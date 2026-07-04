package org.callatis.study.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DiningPhilosophersTest {

    private static final long THREAD_JOIN_TIMEOUT_MS = 3000L;
    private static final int PHILOSOPHER_COUNT = 5;

    private final String implementationName;
    private final Supplier<DiningPhilosophers> implementationFactory;
    private final int n;

    public DiningPhilosophersTest(String implementationName, Supplier<DiningPhilosophers> implementationFactory, int n) {
        this.implementationName = implementationName;
        this.implementationFactory = implementationFactory;
        this.n = n;
    }

    @Parameters(name = "{0}-n={2}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // From DiningPhilosophers.md example.
            {"original", createOriginalFactory(), 1},
            {"simple", createSimpleFactory(), 1},
            // Additional coverage for repeated calls.
            {"original", createOriginalFactory(), 3},
            {"simple", createSimpleFactory(), 3}
        });
    }

    private static Supplier<DiningPhilosophers> createOriginalFactory() {
        return DiningPhilosophersSemaphores::new;
    }

    private static Supplier<DiningPhilosophers> createSimpleFactory() {
        return DiningPhilosophersSimple::new;
    }

    @Test
    public void testDiningPhilosophersActionsAreValid() throws InterruptedException {
        DiningPhilosophers diningPhilosophers = implementationFactory.get();
        List<Operation> operations = new ArrayList<>();
        AtomicReference<Throwable> threadError = new AtomicReference<>();
        CountDownLatch startGate = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();

        for (int philosopher = 0; philosopher < PHILOSOPHER_COUNT; philosopher++) {
            final int philosopherId = philosopher;
            Thread thread = new Thread(() -> runWorker(
                diningPhilosophers,
                operations,
                threadError,
                startGate,
                philosopherId,
                n), "philosopher-" + philosopherId);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        startGate.countDown();
        waitForCompletion(threads);

        if (threadError.get() != null) {
            throw new AssertionError("Worker failed for implementation=" + implementationName, threadError.get());
        }

        assertValidActions(operations, n);
    }

    private static void runWorker(
            DiningPhilosophers diningPhilosophers,
            List<Operation> operations,
            AtomicReference<Throwable> threadError,
            CountDownLatch startGate,
            int philosopher,
            int n) {
        try {
            startGate.await();
            for (int i = 0; i < n; i++) {
                diningPhilosophers.wantsToEat(
                    philosopher,
                    () -> record(operations, philosopher, Fork.LEFT, Action.PICK),
                    () -> record(operations, philosopher, Fork.RIGHT, Action.PICK),
                    () -> record(operations, philosopher, null, Action.EAT),
                    () -> record(operations, philosopher, Fork.LEFT, Action.PUT),
                    () -> record(operations, philosopher, Fork.RIGHT, Action.PUT));
            }
        } catch (InterruptedException t) {
            threadError.compareAndSet(null, t);
            Thread.currentThread().interrupt();
        }
    }

    private static void record(List<Operation> operations, int philosopher, Fork fork, Action action) {
        synchronized (operations) {
            operations.add(new Operation(philosopher, fork, action));
        }
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

    private static void assertValidActions(List<Operation> operations, int n) {
        assertEquals(PHILOSOPHER_COUNT * n * 5, operations.size());

        assertValidActionsWithMapping(operations, n, ForkIndexMapping.A);
        return;
    }

    private static void assertValidActionsWithMapping(List<Operation> operations, int n, ForkIndexMapping preferred) {
        try {
            assertValidActionsInternal(operations, n, preferred);
        } catch (AssertionError first) {
            ForkIndexMapping fallback = (preferred == ForkIndexMapping.A) ? ForkIndexMapping.B : ForkIndexMapping.A;
            try {
                assertValidActionsInternal(operations, n, fallback);
            } catch (AssertionError second) {
                second.addSuppressed(first);
                throw second;
            }
        }
    }

    private static void assertValidActionsInternal(List<Operation> operations, int n, ForkIndexMapping mapping) {
        assertEquals(PHILOSOPHER_COUNT * n * 5, operations.size());

        Map<Integer, Integer> heldForks = new HashMap<>();
        Map<Integer, Integer> actionCountByPhilosopher = new HashMap<>();
        Map<Integer, List<Operation>> byPhilosopher = new HashMap<>();

        for (Operation op : operations) {
            increment(actionCountByPhilosopher, op.philosopher);
            byPhilosopher.computeIfAbsent(op.philosopher, k -> new ArrayList<>()).add(op);

            if (op.action == Action.PICK) {
                int forkIndex = toForkIndex(op.philosopher, op.fork, mapping);
                assertFalse("Fork already held: " + forkIndex, heldForks.containsKey(forkIndex));
                heldForks.put(forkIndex, op.philosopher);
            } else if (op.action == Action.PUT) {
                int forkIndex = toForkIndex(op.philosopher, op.fork, mapping);
                Integer holder = heldForks.get(forkIndex);
                assertEquals("Fork put by non-holder", Integer.valueOf(op.philosopher), holder);
                heldForks.remove(forkIndex);
            } else {
                int leftFork = toForkIndex(op.philosopher, Fork.LEFT, mapping);
                int rightFork = toForkIndex(op.philosopher, Fork.RIGHT, mapping);
                assertEquals(Integer.valueOf(op.philosopher), heldForks.get(leftFork));
                assertEquals(Integer.valueOf(op.philosopher), heldForks.get(rightFork));
            }
        }

        assertTrue("All forks should be released", heldForks.isEmpty());

        for (int philosopher = 0; philosopher < PHILOSOPHER_COUNT; philosopher++) {
            assertEquals(Integer.valueOf(n * 5), actionCountByPhilosopher.get(philosopher));
            assertPerPhilosopherCycle(byPhilosopher.get(philosopher), n);
        }
    }

    private static void assertPerPhilosopherCycle(List<Operation> operations, int n) {
        assertEquals(n * 5, operations.size());
        for (int i = 0; i < operations.size(); i += 5) {
            Operation first = operations.get(i);
            Operation second = operations.get(i + 1);
            Operation third = operations.get(i + 2);
            Operation fourth = operations.get(i + 3);
            Operation fifth = operations.get(i + 4);

            assertEquals(Action.PICK, first.action);
            assertEquals(Action.PICK, second.action);
            assertEquals(Action.EAT, third.action);
            assertEquals(Action.PUT, fourth.action);
            assertEquals(Action.PUT, fifth.action);
            assertTrue(first.fork != second.fork);
            assertTrue(fourth.fork != fifth.fork);
        }
    }

    private static int toForkIndex(int philosopher, Fork fork, ForkIndexMapping mapping) {
        if (mapping == ForkIndexMapping.A) {
            if (fork == Fork.RIGHT) {
                return philosopher;
            }
            return (philosopher + 1) % PHILOSOPHER_COUNT;
        }

        if (fork == Fork.LEFT) {
            return philosopher;
        }
        return (philosopher + 4) % PHILOSOPHER_COUNT;
    }

    private static void increment(Map<Integer, Integer> counts, int key) {
        counts.put(key, counts.getOrDefault(key, 0) + 1);
    }

    private enum Fork {
        LEFT,
        RIGHT
    }

    private enum Action {
        PICK,
        PUT,
        EAT
    }

    private enum ForkIndexMapping {
        A,
        B
    }

    private static final class Operation {
        private final int philosopher;
        private final Fork fork;
        private final Action action;

        private Operation(int philosopher, Fork fork, Action action) {
            this.philosopher = philosopher;
            this.fork = fork;
            this.action = action;
        }
    }
}