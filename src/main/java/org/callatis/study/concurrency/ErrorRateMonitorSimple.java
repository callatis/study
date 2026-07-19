import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ErrorRateMonitorSimple {

    public static final int NUM_EXTRA_BUCKETS = 2;

    public static class ResultCounters {

        private final AtomicLong numErrors = new AtomicLong(0);
        private final AtomicLong numSuccesses = new AtomicLong(0);
        private final AtomicLong generation = new AtomicLong(-1); 

    }

    private final int numBuckets;
    private final AtomicReferenceArray<ResultCounters> ring;

    public ErrorRateMonitorSimple(int numBuckets) {
        this.numBuckets = numBuckets + NUM_EXTRA_BUCKETS;
        ResultCounters[] resultCounters = new ResultCounters[this.numBuckets];
        // L = 5, currSec = 20:36:22
        long currTimeSec = System.nanoTime() / 1_000_000_000;
        for (int i = 0; i < this.numBuckets; i++) {
            // inx = 2
            int inx = (int) (currTimeSec + i) % this.numBuckets;
            resultCounters[inx] = new ResultCounters();
            // 0 = 20:36:25, 1 = 20:36:26, 2 = 20:36:22, 3 = 20:36:23, 4 = 20:36:24
            resultCounters[inx].generation.set(currTimeSec + i);
        }
        this.ring = new AtomicReferenceArray<>(resultCounters);
    }

    // generic SDK to be used by all apps in Walmart
    // error rate monitor
    // a service that has Rest APIs will call this API whenever it services an API
    public void record(boolean isError) {
        long currTimeSec = System.nanoTime() / 1_000_000_000;
        int inx = (int) currTimeSec % this.numBuckets;
        ResultCounters currBucket = this.ring.get(inx);
        if (currBucket.generation.get() < currTimeSec) { // old bucket - reset
            currBucket.numErrors.set(0);
            currBucket.numSuccesses.set(0);
            currBucket.generation.set(currTimeSec);
        }
        if (isError) {
            currBucket.numErrors.incrementAndGet();
        } else {
            currBucket.numSuccesses.incrementAndGet();
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

        long currTimeSec = System.nanoTime() / 1_000_000_000;
        int i = (int) currTimeSec % this.numBuckets;
        long g = this.ring.get(i).generation.get();
        long numErrors = 0, numSuccesses = 0;
        for (int j = 0; j < timeWindowSec; j++) {
            if (i == 0) i = this.numBuckets;
            i--;
            ResultCounters resultCounters = this.ring.get(i);
            if (resultCounters.generation.get() < currTimeSec - this.numBuckets) {
                // old bucket - skip
                continue;
            }
            numErrors += resultCounters.numErrors.get();
            numSuccesses += resultCounters.numSuccesses.get();
        }
        return (numErrors == 0 && numSuccesses == 0) ? 0.0 : (double) numErrors / (numErrors + numSuccesses);
    }

}
