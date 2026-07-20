package org.callatis.study.concurrency;

import java.util.concurrent.atomic.AtomicLong;

public class ErrorRateMonitorSimple {

    public static final int NUM_EXTRA_BUCKETS = 2; // current

    private final int numBuckets;
    private final AtomicLong[] errors;
    private final AtomicLong[] successes;
    private final long nanoStart;

    public ErrorRateMonitorSimple(int numBuckets) {
        this.numBuckets = numBuckets + NUM_EXTRA_BUCKETS;
        this.errors = new AtomicLong[this.numBuckets];
        this.successes = new AtomicLong[this.numBuckets];
        this.nanoStart = System.nanoTime();
        // L = 5, currSec = 20:36:22
        long currTimeSec = (getCurrentTimeInSeconds());
        for (int i = 0; i < this.numBuckets; i++) {
            // inx = 2
            int inx = (int) (currTimeSec + i) % this.numBuckets;
            long generation = currTimeSec + i;
            this.errors[inx] = new AtomicLong(generation << 32); // right 32 bits, representing # errors, are 0
            // 0 = 20:36:25, 1 = 20:36:26, 2 = 20:36:22, 3 = 20:36:23, 4 = 20:36:24
            this.successes[inx] = new AtomicLong(generation << 32);
        }
    }

    // generic SDK to be used by all apps in Walmart
    // error rate monitor
    // a service that has Rest APIs will call this API whenever it services an API
    public void record(boolean isError) {
        long currTimeSec = getCurrentTimeInSeconds();
        if (isError) {
            recordEvent(this.errors, currTimeSec);
        } else {
            recordEvent(this.successes, currTimeSec);
        }
    }

    private void recordEvent(AtomicLong[] events, long currTimeSec) {
        int inx = (int) currTimeSec % this.numBuckets;
        boolean resetNeeded = true;
        while (resetNeeded) {
            long bucketVal = events[inx].get();
            // long rightQuadFull = (1L << 32) - 1;
            long bucketTimeSec = bucketVal >> 32;
            resetNeeded = (bucketTimeSec < currTimeSec);
            if (resetNeeded) {
                // only set it if nobody else incremented it in the meantime
                resetNeeded = !events[inx].compareAndSet(bucketVal, (currTimeSec << 32));
            }
        }
        events[inx].incrementAndGet();
    }

    /**
     * Calls this every minute and would like to know what the error rate was across the last given number of seconds.
     * @param timeWindowSec number of seconds to go back and average - should be <= constructor-given numBuckets - 1. 
     * @return # errors / (# errors + # successes)
     */
    public double getErrorRate(long timeWindowSec) {
        if (timeWindowSec > this.numBuckets - NUM_EXTRA_BUCKETS - 1) {
            timeWindowSec = this.numBuckets - NUM_EXTRA_BUCKETS - 1;
        }

        long currTimeSec = getCurrentTimeInSeconds();
        int i = (int) currTimeSec % this.numBuckets;
        long rightQuadFull = (1L << 32) - 1;
        long numErrors = 0, numSuccesses = 0;
        for (int j = 0; j < timeWindowSec; j++) {
            if (i == 0) i = this.numBuckets;
            i--;
            long e = this.errors[i].get();
            if ((e >> 32) >= currTimeSec - this.numBuckets) {
                numErrors += e & rightQuadFull;
            }
            long s = this.successes[i].get();
            if ((s >> 32) >= currTimeSec - this.numBuckets) {
                numSuccesses += s & rightQuadFull;
            }
        }
        return (numErrors == 0 && numSuccesses == 0) ? 0.0 : (double) numErrors / (numErrors + numSuccesses);
    }

    private long getCurrentTimeInSeconds() {
        return ((System.nanoTime() - this.nanoStart) / 1_000_000_000L) % (1L << 31);
    }

}
