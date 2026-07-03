package org.callatis.study.concurrency;

public class H2OSimple {

    public H2OSimple() {
        
    }

    private int h = 0, o = 0;

    private String getState() {
        return "H" + this.h + "O" + this.o;
    }    

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {
        
        synchronized (this) {

            String thrName = Thread.currentThread().getName() + ":-H" + (this.h + 1);
            System.out.println(thrName + " starting");
            while (this.h - 2 * this.o >= 2) {
                System.out.println(thrName + " waiting for O at " + getState());
                wait();
            }

            System.out.println(thrName + " releases: H");
            // releaseHydrogen.run() outputs "H". Do not change or remove this line.
            releaseHydrogen.run();
            this.h++;

            // yield control and wake up someone else
            System.out.println(thrName + " yielding at " + getState());
            notifyAll();
            // this.needsOxy.signal();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {
        synchronized (this) { 

            String thrName = Thread.currentThread().getName() + ":-O" + (this.o + 1);
            System.out.println(thrName + " starting");
            while (this.h - 2 * this.o < 2) {
                System.out.println(thrName + " waiting for H at " + getState());
                wait();
            }

            System.out.println(thrName + " releases: O");
            // releaseOxygen.run() outputs "O". Do not change or remove this line.
            releaseOxygen.run();
            this.o++; 
            
            System.out.println(thrName + " yielding at " + getState());
            // yield control and wake up someone else
            notifyAll(); // dies and notifies a second H thread
        }
    }

}