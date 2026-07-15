package org.callatis.study.concurrency;

public class TokenBucket {

    protected final long capacity;

    protected final long refillTokens;

    protected final double refillPeriodNS;

    private final TokenBucket delegate;

    public TokenBucket(boolean withCAS, long capacity, long refillTokens, long refillPeriodMillis) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodNS = refillPeriodMillis * 1_000_000.0;
        this.delegate = withCAS
            ? new TokenBucketCAS(capacity, refillTokens, refillPeriodMillis)
            : new TokenBucketBasic(capacity, refillTokens, refillPeriodMillis);
    }

    protected TokenBucket(long capacity, long refillTokens, long refillPeriodMillis) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodNS = refillPeriodMillis * 1_000_000.0;
        this.delegate = null;
    }

    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    public boolean tryAcquire(int permits) {
        return this.delegate.tryAcquire(permits);
    }

}
