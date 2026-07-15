package org.callatis.study.concurrency;

class TokenBucketBasic extends TokenBucket {

    private double k; // numTokens

    private long lastNS;

    TokenBucketBasic(long capacity, long refillTokens, long refillPeriodMillis) {
        super(capacity, refillTokens, refillPeriodMillis);
        this.k = capacity;
        this.lastNS = System.nanoTime();
    }

    @Override
    public synchronized boolean tryAcquire(int permits) {
        long currNS = System.nanoTime();
        long periodNS = (currNS - lastNS);
        this.k += (periodNS / this.refillPeriodNS) * refillTokens;
        this.lastNS = currNS;
        this.k = Math.min(k, (double) this.capacity);
        if (this.k >= permits) {
            this.k-= permits;
            return true;
        }
        return false;
    }

}
