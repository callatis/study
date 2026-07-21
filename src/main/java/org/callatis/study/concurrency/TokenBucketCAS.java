package org.callatis.study.concurrency;

import java.util.concurrent.atomic.AtomicReference;

class TokenBucketCAS extends TokenBucket {

    private static class State {

        private final double k;

        private final long lastNS;

        private State(double k, long lastNS) {
            this.k = k;
            this.lastNS = lastNS;
        }

    }

    private static final int NUM_WAIT_ITERATIONS = 100;

    private final AtomicReference<State> state;

    TokenBucketCAS(long capacity, long refillTokens, long refillPeriodMillis) {
        super(capacity, refillTokens, refillPeriodMillis);
        this.state = new AtomicReference<>(new State((double) capacity, System.nanoTime()));
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean tryAcquire(int permits, boolean block) {
        for (int i = 0; i < NUM_WAIT_ITERATIONS; i++) {
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
                if (result || !block) return result; // We either got thru or we don't need to block
                // we need to block and we did not get thru
                double needed = (double) permits - kk;
                long timeNeeded = (long) Math.ceil(needed * this.tokenRefillTime);
                try {
                    Thread.sleep(timeNeeded / 1_000_000, (int) timeNeeded % 1_000_000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return false;
    }

}
