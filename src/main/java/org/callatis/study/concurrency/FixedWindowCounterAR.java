package org.callatis.study.concurrency;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Fixed Window Counter rate limiter.
 *
 * <p>Divides time into consecutive, non-overlapping windows of fixed length
 * {@code windowMillis}. Each window has its own counter starting at zero. A
 * request in the current window is admitted if the window's counter is below
 * {@code maxRequests}, incrementing that counter; when the clock crosses into a
 * new window the counter resets to zero. Windows are aligned to absolute time:
 * the window index for an instant {@code now} is {@code now / windowMillis}.
 *
 * <p>Thread-safety invariant: within any single window
 * {@code [k*W, (k+1)*W)}, the number of requests granted never exceeds
 * {@code maxRequests}. The check-window / reset / increment sequence must be a
 * single atomic step.
 *
 * <p>Based on the problem statement in
 * {@code src/docs/org/callatis/study/concurrency/RateLimiterProblems.md}
 * (section 1, "Design a Fixed Window Counter Rate Limiter"). Normally this
 * class would be backed by a dedicated {@code FixedWindowCounter.md}, but the
 * fixed-window problem is authored inside the shared RateLimiterProblems
 * document.
 * 
 * Note: this implementation packs the bucket and the counter in an AtomicReference. 
 */
public class FixedWindowCounterAR {

    protected static class Snapshot {
        protected long counter = 0;
        protected long bucket = 0;

        public Snapshot(long counter, long bucket) {
            this.counter = counter;
            this.bucket = bucket;
        }
    }

    protected final int maxRequests;

    protected final long windowMillis;

    protected final long startNanoTime = System.nanoTime();

    protected final AtomicReference<Snapshot> state = new AtomicReference<>(new Snapshot(0, 0));

    /**
     * @param maxRequests  the limit of admitted requests per window
     * @param windowMillis the fixed window length in milliseconds
     */
    public FixedWindowCounterAR(int maxRequests, long windowMillis) {
        this.maxRequests = maxRequests;
        this.windowMillis = windowMillis;
    }

    /**
     * Determines the current window, resetting the count to zero if the clock
     * has advanced into a new window since the last call. If the current
     * window's count is below {@code maxRequests}, admits the request
     * (increments the count) and returns {@code true}; otherwise returns
     * {@code false} without incrementing.
     *
     * @return {@code true} if the request was admitted, {@code false} otherwise
     */
    public boolean tryAcquire() {
        while (true) { 
            long currMS = (System.nanoTime() - this.startNanoTime) / 1_000_000L;
            long bucket = currMS / this.windowMillis;
            Snapshot currState = this.state.get();
            long currCounter = currState.counter;
            long currBucket = currState.bucket;
            if (bucket > currBucket) { // needs a new window
                Snapshot newState = new Snapshot(1, bucket);
                if (this.state.compareAndSet(currState, newState)) {
                    return true;
                }
            } else { // still in the old window
                if (currCounter >= this.maxRequests) {
                    return false;
                }
                Snapshot newState = new Snapshot(currCounter + 1, bucket);
                if (this.state.compareAndSet(currState, newState)) {
                    return true;
                }
            }
        }
    }

}
