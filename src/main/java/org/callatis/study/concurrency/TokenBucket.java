package org.callatis.study.concurrency;

import java.util.concurrent.atomic.AtomicReference;

public class TokenBucket {

    private static class State {

        private final double k;

        private final long lastNS;

        private State(double k, long lastNS) {
            this.k = k;
            this.lastNS = lastNS;
        }

    }

    private final long capacity;

    private final long refillTokens;

    private final double refillPeriodNS;

    private final boolean withCAS;

    private double k; // numTokens

    private long lastNS;

    private AtomicReference<State> state = null;

    public TokenBucket(boolean withCAS, long capacity, long refillTokens, long refillPeriodMillis) {
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodNS = refillPeriodMillis * 1_000_000.0;

        this.withCAS = withCAS;
        if (this.withCAS) {
            this.state = new AtomicReference<TokenBucket.State>(new State(this.capacity, System.nanoTime()));
        } else {
            this.k = capacity;
            this.lastNS = System.nanoTime();

        }
    }

    public boolean tryAcquire() {
        return tryAcquire(1);
    }

    public boolean tryAcquire(int permits) {
        return this.withCAS ? tryAcquireCAS(permits) : tryAcquireBasic(permits);
    }

    private synchronized boolean tryAcquireBasic(int permits) {
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

    private boolean tryAcquireCAS(int permits) {
        for (int i = 0; i < 100; i++) {
            boolean result = false;
            State myState = this.state.get();
            long myLastNS = myState.lastNS;
            long currNS = System.nanoTime();
            long periodNS = (currNS - myLastNS);
            double kk = Math.min(myState.k + (periodNS / this.refillPeriodNS) * refillTokens, (double) this.capacity);
            if (kk >= permits) {
                kk-= permits;
                result = true;
            } 
            State currState = new State(kk, currNS);
            if (this.state.compareAndSet(myState, currState)) {
                return result;
            }
        }

        return false;
    }

}
