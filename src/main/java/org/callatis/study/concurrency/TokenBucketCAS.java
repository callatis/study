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

    private final AtomicReference<State> state;

    TokenBucketCAS(long capacity, long refillTokens, long refillPeriodMillis) {
        super(capacity, refillTokens, refillPeriodMillis);
        this.state = new AtomicReference<State>(new State((double) capacity, System.nanoTime()));
    }

    @Override
    public boolean tryAcquire(int permits) {
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
