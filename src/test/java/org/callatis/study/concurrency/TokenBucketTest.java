package org.callatis.study.concurrency;

import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TokenBucketTest {

    @Test
    public void testStartsFullAndAllowsBurst() {
        TokenBucket bucket = new TokenBucket(5, 1, 1000);

        assertTrue(bucket.tryAcquire());
        assertTrue(bucket.tryAcquire());
        assertTrue(bucket.tryAcquire(3));
    }

    @Test
    public void testRefusesWhenEmptyWithoutConsuming() {
        TokenBucket bucket = new TokenBucket(5, 1, 1000);

        assertTrue("Refused when at full, untouched capacity", bucket.tryAcquire(5));
        assertFalse("Allowed when drained", bucket.tryAcquire());
    }

    @Test
    public void testAccruesTokensOverTime() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(5, 1, 1000);

        assertTrue(bucket.tryAcquire(5));
        assertFalse(bucket.tryAcquire());

        Thread.sleep(2000L);
        assertTrue(bucket.tryAcquire(2));
    }

    @Test
    public void testFractionalAccrualSurvivesFailedCall() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(5, 1, 1000);

        assertTrue(bucket.tryAcquire(5));

        Thread.sleep(500L);
        assertFalse("only 0.5 token accrued, cannot grant 1", bucket.tryAcquire());

        Thread.sleep(600L);
        assertTrue("at least a full token accrued by now", bucket.tryAcquire());
    }

    @Test
    public void testCapacityIsHardCeiling() throws InterruptedException {
        TokenBucket bucket = new TokenBucket(3, 1, 2000);

        // Idle far longer than it takes to accrue `capacity` tokens: an
        // uncapped bucket would hold ~8, a correctly capped one stays at 3.
        Thread.sleep(5000L);
        assertTrue("bucket was not at capacity", bucket.tryAcquire(3));

        // At 1 token/2 sec there is ~2s of headroom before another token
        // accrues, so this back-to-back call robustly finds an empty bucket.
        assertFalse("bucket must not exceed capacity", bucket.tryAcquire());
    }

    @Test
    public void testConcurrentInvariantNeverOverGrants() throws InterruptedException {
        long capacity = 1000;
        long refillTokens = 1;
        long refillPeriodMillis = 1000;
        TokenBucket bucket = new TokenBucket(capacity, refillTokens, refillPeriodMillis);

        int threadCount = 64;
        long durationMillis = 500L;
        AtomicLong totalGranted = new AtomicLong(0);
        Thread[] threads = new Thread[threadCount];

        long startNanos = System.nanoTime();
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                long localGranted = 0;
                while (System.nanoTime() - startNanos < durationMillis * 1_000_000L) {
                    if (bucket.tryAcquire()) {
                        localGranted++;
                    }
                }
                totalGranted.addAndGet(localGranted);
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
        long elapsedNanos = System.nanoTime() - startNanos;

        double refillRatePerNano = (double) refillTokens / (refillPeriodMillis * 1_000_000L);
        long bound = capacity + (long) Math.ceil(elapsedNanos * refillRatePerNano);
        assertTrue(
            "over-granted: granted=" + totalGranted.get() + " bound=" + bound,
            totalGranted.get() <= bound);
    }
}
