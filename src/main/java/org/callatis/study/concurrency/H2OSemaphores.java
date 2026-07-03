package org.callatis.study.concurrency;

import java.util.concurrent.Semaphore;

public class H2OSemaphores {

    // private int h = 0, o = 0;

    private final Semaphore hSem = new Semaphore(2);

    private final Semaphore oSem = new Semaphore(0);

    // private String getState() {
    //     return "H" + this.h + "_O" + this.o;
    // }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        
        // String thrName = Thread.currentThread().getName() + "__H" + (this.h + 1);
        // System.out.println(thrName + " starting");
        this.hSem.acquire();
        try {
            // System.out.println(thrName + " RELEASES: H");
            // this.h++;
            
            // releaseHydrogen.run() outputs "H". Do not change or remove this line.
            releaseHydrogen.run();
            
            // yield control and wake up someone else
            // System.out.println(thrName + " ending at " + getState());
        } finally {
            this.oSem.release();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        
        // String thrName = Thread.currentThread().getName() + "__O" + (this.o + 1);
        // System.out.println(thrName + " starting");
        this.oSem.acquire(2);
        try {
            // System.out.println(thrName + " RELEASES: O");
            // releaseOxygen.run() outputs "O". Do not change or remove this line.
            releaseOxygen.run();
            // this.o++; 
            
            // System.out.println(thrName + " ending at " + getState());
        } finally {
            this.hSem.release(2);
        }
    }

}