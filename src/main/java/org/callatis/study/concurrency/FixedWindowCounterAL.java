package org.callatis.study.concurrency;

import java.util.concurrent.atomic.AtomicLong;

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
 * Note: this implementation packs the bucket and the counter in an AtomicLong. 
 */
public class FixedWindowCounterAL {

    protected static final long RIGHT_INT_MASK = (1L << 32) - 1L;

    protected static final long LEFT_INT_MASK = RIGHT_INT_MASK << 32;

    protected final int maxRequests;

    protected final long windowMillis;

    protected final long startNanoTime = System.nanoTime();

    protected final AtomicLong state = new AtomicLong();

    /**
     * @param maxRequests  the limit of admitted requests per window
     * @param windowMillis the fixed window length in milliseconds
     */
    public FixedWindowCounterAL(int maxRequests, long windowMillis) {
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

            long currState = this.state.get();
            long currBucket = (currState & LEFT_INT_MASK) >> 32;
            int currCounter = (int) (currState & RIGHT_INT_MASK);

            if (bucket > currBucket) { // we need to advance the bucket
                long newState = (bucket << 32) | 1L;
                if (this.state.compareAndSet(currState, newState)) return true;
            } else if (currCounter >= this.maxRequests) {
                return false;
            } else {
                long newState = (currBucket << 32) | (currCounter + 1);
                if (this.state.compareAndSet(currState, newState)) {
                    return true;
                }
            }
        }
    }

}
