package org.callatis.study.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class H2O {

    private int h = 0, o = 0;

    private final Lock lock = new ReentrantLock();

    private final Condition needsOxy = lock.newCondition();

    private final Condition needsHydro = lock.newCondition();

    private String getState() {
        return "H" + this.h + "O" + this.o;
    }

    public H2O() {
        
    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        lock.lock();
        try {

            String thrName = Thread.currentThread().getName() + ":-H" + (this.h + 1);
            System.out.println(thrName + " starting");
            while (this.h - 2 * this.o >= 2) {
                System.out.println(thrName + " waiting for O at " + getState());
                this.needsOxy.await();
            }

            System.out.println(thrName + " releases: H");
            // releaseHydrogen.run() outputs "H". Do not change or remove this line.
            releaseHydrogen.run();
            this.h++;

            // yield control and wake up someone else
            System.out.println(thrName + " yielding at " + getState());
            this.needsHydro.signal();
            // this.needsOxy.signal();
        } finally {
            lock.unlock();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        lock.lock();
        try { 

            String thrName = Thread.currentThread().getName() + ":-O" + (this.o + 1);
            System.out.println(thrName + " starting");
            while (this.h - 2 * this.o < 2) {
                System.out.println(thrName + " waiting for H at " + getState());
                this.needsHydro.await();
            }

            System.out.println(thrName + " releases: O");
            // releaseOxygen.run() outputs "O". Do not change or remove this line.
            releaseOxygen.run();
            this.o++; 
            
            System.out.println(thrName + " yielding at " + getState());
            // yield control and wake up someone else
            this.needsOxy.signal(); // dies and notifies one H thread
            this.needsOxy.signal(); // dies and notifies a second H thread
        } finally {
            lock.unlock();
        }
    }

}