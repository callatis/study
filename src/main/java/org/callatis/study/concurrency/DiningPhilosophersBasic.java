package org.callatis.study.concurrency;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Simplest correct solution: a single global lock serializes the entire
 * pick/eat/put sequence so only one philosopher acts at a time. This trivially
 * satisfies fork exclusivity, cannot deadlock, and never emits interleaved
 * (racing) callback events.
 */
public class DiningPhilosophersBasic implements DiningPhilosophers {

    private final Lock lock = new ReentrantLock();

    @Override
    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {

        this.lock.lock();
        try {
            pickLeftFork.run();
            pickRightFork.run();
            eat.run();
            putLeftFork.run();
            putRightFork.run();
        } finally {
            this.lock.unlock();
        }
    }

}
