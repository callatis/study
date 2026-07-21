package org.callatis.study.concurrency;

/**
 * A rate limiter that admits or rejects a single request at the current instant.
 */
public interface RateLimiter {

    /**
     * @return {@code true} if the request is admitted, {@code false} otherwise
     */
    boolean tryAcquire();

}
