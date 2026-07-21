package org.callatis.study.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Timing-based tests for every {@link ErrorRateMonitor} implementation, run in a
 * parameterized fashion against {@link ErrorRateMonitorAL}, {@link ErrorRateMonitorAR}
 * and {@link ErrorRateMonitorSweeper}.
 *
 * <p>Each monitor divides time into one-second buckets from a start captured at
 * construction, so these tests do not align to any wall-clock boundary: they
 * record into the fresh monitor's opening second (second 0) and sleep across
 * whole seconds to roll buckets over. They are inherently timing-dependent and
 * assume the opening second does not roll over during the microseconds of setup.
 */
@RunWith(Parameterized.class)
public class ErrorRateMonitorTest {

    private static final double EPS = 1e-9;

    /**
     * Builds a monitor for the given window size, registering any background
     * resources (e.g. the Sweeper's scheduler) so the test can shut them down.
     */
    interface MonitorFactory {
        ErrorRateMonitor create(int numBuckets, List<ScheduledExecutorService> resources);
    }

    private final MonitorFactory factory;
    private final List<ScheduledExecutorService> resources = new ArrayList<>();

    public ErrorRateMonitorTest(String label, MonitorFactory factory) {
        this.factory = factory;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            {"AL", (MonitorFactory) (n, res) -> new ErrorRateMonitorAL(n)},
            {"AR", (MonitorFactory) (n, res) -> new ErrorRateMonitorAR(n)},
            {"Sweeper", (MonitorFactory) (n, res) -> {
                ScheduledExecutorService svc = Executors.newSingleThreadScheduledExecutor();
                res.add(svc);
                return new ErrorRateMonitorSweeper(n, svc);
            }},
        });
    }

    private ErrorRateMonitor newMonitor(int numBuckets) {
        return factory.create(numBuckets, resources);
    }

    @After
    public void shutdownResources() {
        for (ScheduledExecutorService svc : resources) {
            svc.shutdownNow();
        }
        resources.clear();
    }

    @Test
    public void emptyWindowReturnsZero() {
        ErrorRateMonitor m = newMonitor(10);
        assertEquals(0.0, m.getErrorRate(5), 0.0);
    }

    @Test
    public void singleBucketRate() throws InterruptedException {
        ErrorRateMonitor m = newMonitor(10);
        m.record(false);
        m.record(true);
        m.record(false);
        Thread.sleep(1100);                 // roll out of the opening second
        assertEquals(1.0 / 3.0, m.getErrorRate(1), EPS);
    }

    @Test
    public void currentInFlightSecondIsExcluded() {
        ErrorRateMonitor m = newMonitor(10);
        m.record(true);
        // Still inside the opening second, which getErrorRate never counts.
        assertEquals(0.0, m.getErrorRate(1), 0.0);
    }

    @Test
    public void dataExpiresFromTheWindow() throws InterruptedException {
        ErrorRateMonitor m = newMonitor(10);
        m.record(true);
        Thread.sleep(3000);
        assertEquals(1.0, m.getErrorRate(5), EPS);   // still inside a 5s window
        assertEquals(0.0, m.getErrorRate(1), 0.0);   // aged out of a 1s window
    }

    @Test
    public void idleGapsAreSkippedNotCounted() throws InterruptedException {
        ErrorRateMonitor m = newMonitor(10);
        m.record(true);
        Thread.sleep(2000);
        m.record(false);
        Thread.sleep(1100);
        // one error, one success; the empty second in between contributes nothing
        assertEquals(0.5, m.getErrorRate(5), EPS);
    }

    @Test
    public void concurrentWritersLoseNothing() throws InterruptedException {
        ErrorRateMonitor m = newMonitor(300);
        int threads = 8, perThread = 25_000;
        AtomicLong expectedErrors = new AtomicLong();

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch done = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            final int seed = t;
            pool.execute(() -> {
                for (int i = 0; i < perThread; i++) {
                    boolean err = ((i + seed) % 4 == 0);
                    if (err) expectedErrors.incrementAndGet();
                    m.record(err);
                }
                done.countDown();
            });
        }
        assertTrue(done.await(30, TimeUnit.SECONDS));
        pool.shutdown();

        Thread.sleep(1200);                 // flush the in-flight second
        long total = threads * (long) perThread;
        double expected = expectedErrors.get() / (double) total;
        assertEquals(expected, m.getErrorRate(250), 1e-6);
    }

    @Test
    public void concurrentReadersDoNotDisturbEachOther() throws Exception {
        ErrorRateMonitor m = newMonitor(20);
        for (int i = 0; i < 1000; i++) {
            m.record(i % 5 == 0);           // 200 errors, 800 successes -> 0.2
        }
        Thread.sleep(1100);

        ExecutorService pool = Executors.newFixedThreadPool(8);
        Future<?>[] fs = new Future<?>[8];
        for (int i = 0; i < 8; i++) {
            fs[i] = pool.submit(() -> {
                for (int k = 0; k < 200; k++) {
                    assertEquals(0.2, m.getErrorRate(10), EPS);
                }
            });
        }
        for (Future<?> f : fs) {
            f.get(30, TimeUnit.SECONDS);
        }
        pool.shutdown();
    }

    @Test
    public void windowLongerThanSupportedIsClamped() {
        ErrorRateMonitor m = newMonitor(10);
        // Must clamp instead of reading past the ring (no exception, empty -> 0.0).
        assertEquals(0.0, m.getErrorRate(100_000), 0.0);
    }
}
