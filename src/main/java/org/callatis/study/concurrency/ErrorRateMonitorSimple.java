package org.callatis.study.concurrency;

import java.util.concurrent.atomic.AtomicLong;

public class ErrorRateMonitorSimple {

    public static final int NUM_EXTRA_BUCKETS = 2; // current
    private static final long RIGHT_INT_BITMASK = (1L << 32) - 1;

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
        long currTimeSec = getCurrentTimeInSeconds();
        for (int i = 0; i < this.numBuckets; i++) {
            // inx = 2
            int inx = (int) (currTimeSec + i) % this.numBuckets;
            long bucketTimeSec = currTimeSec + i;
            this.errors[inx] = new AtomicLong(bucketTimeSec << 32); // right 32 bits, representing # errors, are 0
            // 0 = 20:36:25, 1 = 20:36:26, 2 = 20:36:22, 3 = 20:36:23, 4 = 20:36:24
            this.successes[inx] = new AtomicLong(bucketTimeSec << 32);
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
        while (true) {
            long currSnapshot = events[inx].get();
            long bucketTimeSec = currSnapshot >> 32;
            long newSnapshot = currSnapshot;
            if (bucketTimeSec < currTimeSec) { // we're in an old bucket - attempt to set a new bucket with a counter = 1
                newSnapshot = (currTimeSec << 32) | 1L;
            } else { // we're in the right bucket - just increment the counter, which is the same as incrementing the snapshot
                newSnapshot++;
            }
            if (events[inx].compareAndSet(currSnapshot, newSnapshot)) {
                return;
            }
        }
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

        final long currTimeSec = getCurrentTimeInSeconds();
        int bucket = (int) currTimeSec % this.numBuckets;
        long numErrors = 0, numSuccesses = 0;
        for (int j = 0; j < timeWindowSec; j++) {
            if (bucket == 0) bucket = this.numBuckets;
            bucket--;
            numErrors += addNumEvents(this.errors, bucket, currTimeSec);
            numSuccesses += addNumEvents(this.successes, bucket, currTimeSec);
        }
        return (numErrors == 0 && numSuccesses == 0) ? 0.0 : (double) numErrors / (numErrors + numSuccesses);
    }

    private long addNumEvents(AtomicLong[] events, int bucket, final long currTimeSec) {
        long snapshot = events[bucket].get();
        if ((snapshot >> 32) >= currTimeSec - this.numBuckets) { // active bucket
            return snapshot & RIGHT_INT_BITMASK; // extract the counter as the right int
        }
        // obsolete bucket, ignore
        return 0;
    }

    private long getCurrentTimeInSeconds() {
        return ((System.nanoTime() - this.nanoStart) / 1_000_000_000L) % (1L << 31);
    }

}
