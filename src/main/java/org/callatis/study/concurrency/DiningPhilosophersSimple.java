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

    void pickFork(int id, Runnable pick) {
        this.locks[id].lock();
        // System.out.println("Pick up fork " + id);
        pick.run();
    }

    void putFork(int id, Runnable put) {
        put.run();
        // System.out.println("Drop fork " + id);
        this.locks[id].unlock();
    }

    @Override
    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        
        int leftFork = philosopher;
        int rightFork = (philosopher + 4) % 5;

        this.totalSem.acquire();
        pickFork(leftFork, pickLeftFork);
        pickFork(rightFork, pickRightFork);
        eat.run();
        putFork(rightFork, putRightFork);
        putFork(leftFork, putLeftFork);

        this.totalSem.release();
    }

}
