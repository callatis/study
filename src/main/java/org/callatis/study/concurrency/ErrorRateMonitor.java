package org.callatis.study.concurrency;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ErrorRateMonitor {

    public static final int NUM_EXTRA_BUCKETS = 2;

    public static class ResultCounters {

        private final AtomicLong numErrors = new AtomicLong(0);
        private final AtomicLong numSuccesses = new AtomicLong(0);
        private final AtomicLong generation = new AtomicLong(-1); 

    }

    private final int numBuckets;
    private final AtomicReferenceArray<ResultCounters> ring;
    private final AtomicInteger currInx = new AtomicInteger(0);
    private final Runnable advanceBucket;
    private final AtomicInteger delInx = new AtomicInteger(1);

    public ErrorRateMonitor(int numBuckets, ScheduledExecutorService execSvc) {
        this.numBuckets = numBuckets + NUM_EXTRA_BUCKETS;
        ResultCounters[] resultCounters = new ResultCounters[this.numBuckets];
        for (int i = 0; i < this.numBuckets; i++) {
            resultCounters[i] = new ResultCounters();
            resultCounters[i].generation.set((i + this.numBuckets - 2) % this.numBuckets);
        }
        this.ring = new AtomicReferenceArray<>(resultCounters);
        this.advanceBucket = () -> {
            if (!this.delInx.compareAndSet(this.numBuckets - 1, 0)) {
                this.delInx.incrementAndGet();
            }
            ResultCounters delResultCounters = this.ring.get(this.delInx.get());
            delResultCounters.generation.set(delResultCounters.generation.get() + this.numBuckets);
            delResultCounters.numErrors.set(0);
            delResultCounters.numSuccesses.set(0);
            if (!this.currInx.compareAndSet(this.numBuckets - 1, 0)) {
                this.currInx.incrementAndGet();
            }
        };
        execSvc.scheduleAtFixedRate(this.advanceBucket, 1_000, 1_000, TimeUnit.MILLISECONDS);
    }

    // generic SDK to be used by all apps in Walmart
    // error rate monitor
    // a service that has Rest APIs will call this API whenever it services an API
    public void record(boolean isError) {
        ResultCounters currBucket = this.ring.get(this.currInx.get());
        if (isError) {
            currBucket.numErrors.incrementAndGet();
        } else {
            currBucket.numSuccesses.incrementAndGet();
        }
    }

    /**
     * Calls this every minute and would like to know what the error rate was across the last given number of seconds.
     * @param timeWindowSec number of seconds to go back and average - should not be larger than the constructor-given numBuckets - 1. 
     * @return # errors / (# errors + # successes)
     */
    public double getErrorRate(long timeWindowSec) {
        if (timeWindowSec > this.numBuckets - NUM_EXTRA_BUCKETS - 1) {
            timeWindowSec = this.numBuckets - NUM_EXTRA_BUCKETS - 1;
        }
        int i = this.currInx.get();
        long g = this.ring.get(i).generation.get();
        long numErrors = 0, numSuccesses = 0;
        for (int j = 0; j < timeWindowSec; j++) {
            if (i == 0) i = this.numBuckets;
            i--;
            ResultCounters resultCounters = this.ring.get(i);
            if (resultCounters.generation.get() > g) {
                // sweeper reached this index - skip
                continue;
            }
            long e = resultCounters.numErrors.get();
            long s = resultCounters.numSuccesses.get();
            if (resultCounters.generation.get() <= g) { // bucket didn't get cleared in the middle of the read
                numErrors += e;
                numSuccesses += s;
            }
        }
        return (numErrors == 0 && numSuccesses == 0) ? 0.0 : (double) numErrors / (numErrors + numSuccesses);
    }
    
}
