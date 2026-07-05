package org.callatis.study.concurrency;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersSimple implements DiningPhilosophers {

    private final Semaphore totalSem = new Semaphore(4);
    private final Lock[] locks = new ReentrantLock[] {
        new ReentrantLock(), 
        new ReentrantLock(), 
        new ReentrantLock(), 
        new ReentrantLock(), 
        new ReentrantLock()
    };

    @Override
    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        
        int leftFork = philosopher;
        int rightFork = (philosopher + 1) % 5;

        this.totalSem.acquire();
        if ((philosopher & 1) == 0) {
            this.locks[leftFork].lock();
            this.locks[rightFork].lock();
        } else {
            this.locks[rightFork].lock();
            this.locks[leftFork].lock();
        }

        pickLeftFork.run();
        pickRightFork.run();
        eat.run();
        putLeftFork.run();
        putRightFork.run();

        this.locks[rightFork].unlock();
        this.locks[leftFork].unlock();

        this.totalSem.release();
    }

}
