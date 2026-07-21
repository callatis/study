package org.callatis.study.concurrency;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FixedWindowCounterTest {

    private final Class<? extends RateLimiter> impl;

    public FixedWindowCounterTest(Class<? extends RateLimiter> impl) {
        this.impl = impl;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            {FixedWindowCounterAL.class},
            {FixedWindowCounterAR.class}
        });
    }

    private RateLimiter newCounter(int maxRequests, long windowMillis) {
        try {
            return impl.getConstructor(int.class, long.class)
                       .newInstance(maxRequests, windowMillis);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void testAdmitsUpToLimitWithinWindow() {
        RateLimiter counter = newCounter(3, 10_000);

        assertTrue(counter.tryAcquire());
        assertTrue(counter.tryAcquire());
        assertTrue(counter.tryAcquire());
    }

    @Test
    public void testRejectsBeyondLimitWithinWindow() {
        RateLimiter counter = newCounter(3, 10_000);

        assertTrue(counter.tryAcquire());
        assertTrue(counter.tryAcquire());
        assertTrue(counter.tryAcquire());
        assertFalse("fourth request in the same window must be rejected", counter.tryAcquire());
    }

    @Test
    public void testRejectionDoesNotConsume() {
        RateLimiter counter = newCounter(1, 10_000);

        assertTrue(counter.tryAcquire());
        assertFalse(counter.tryAcquire());
        assertFalse("a rejected call must not admit later within the same window", counter.tryAcquire());
    }

    @Test
    public void testCounterResetsOnNewWindow() throws InterruptedException {
        RateLimiter counter = newCounter(2, 500);

        assertTrue(counter.tryAcquire());
        assertTrue(counter.tryAcquire());
        assertFalse("window is full", counter.tryAcquire());

        // Cross into the next fixed window; the counter must reset to zero.
        Thread.sleep(700L);
        assertTrue("new window should admit again", counter.tryAcquire());
        assertTrue(counter.tryAcquire());
        assertFalse("new window fills at the same limit", counter.tryAcquire());
    }

    @Test
    public void testConcurrentInvariantNeverOverGrantsPerWindow() throws InterruptedException {
        int maxRequests = 100;
        long windowMillis = 50;
        RateLimiter counter = newCounter(maxRequests, windowMillis);

        int threadCount = 64;
        long durationMillis = 500L;
        // Bucket successful grants by absolute window index: now / windowMillis.
        Map<Long, AtomicLong> grantsPerWindow = new ConcurrentHashMap<>();
        Thread[] threads = new Thread[threadCount];

        long startNanos = System.nanoTime();
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                while (System.nanoTime() - startNanos < durationMillis * 1_000_000L) {
                    if (counter.tryAcquire()) {
                        long windowIndex = System.currentTimeMillis() / windowMillis;
                        grantsPerWindow
                            .computeIfAbsent(windowIndex, k -> new AtomicLong(0))
                            .incrementAndGet();
                    }
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }

        // A window boundary may be crossed while bucketing, so allow the two
        // adjacent windows a request could straddle: assert no window exceeds
        // twice the limit as a coarse-but-safe upper bound on lost updates.
        for (Map.Entry<Long, AtomicLong> e : grantsPerWindow.entrySet()) {
            long granted = e.getValue().get();
            assertTrue(
                "over-granted in window " + e.getKey() + ": granted=" + granted
                    + " maxRequests=" + maxRequests,
                granted <= 2L * maxRequests);
        }
    }
}
