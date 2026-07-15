package org.callatis.study.concurrency;

public class TokenBucket {

    private final long capacity;

    private final long refillTokens;

    private final double refillPeriodNS;

    private double k; // numTokens

    private long lastNS;

    public TokenBucket(long capacity, long refillTokens, long refillPeriodMillis) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodNS = refillPeriodMillis * 1_000_000.0;

        this.k = capacity;
        this.lastNS = System.nanoTime();
    }

    public boolean tryAcquire() {
        return tryAcquire(1);
    }

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
