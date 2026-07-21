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
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean tryAcquire(int permits, boolean block) {
        while (true) {
            long timeNeeded = 0;
            synchronized (this) {
                long currNS = System.nanoTime();
                long periodNS = (currNS - lastNS);
                this.k += (periodNS / this.refillPeriodNS) * refillTokens;
                this.lastNS = currNS;
                this.k = Math.min(k, (double) this.capacity);
                if (this.k >= permits) {
                    this.k-= permits;
                    return true;
                }
                if (!block) return false;
                double needed = (double) permits - this.k;
                timeNeeded = (long) Math.ceil(needed * this.tokenRefillTime);
            }
            try {
                Thread.sleep(timeNeeded / 1_000_000, (int) timeNeeded % 1_000_000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
