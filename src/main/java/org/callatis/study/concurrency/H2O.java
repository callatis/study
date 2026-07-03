package org.callatis.study.concurrency;

public class H2O {

    private int h = 0, o = 0;

    private int hh = 0, oo = 0;

    private boolean releasing = false;

    private boolean isReadyToRelease() {
        return this.h >= 2 && this.o > 0;
    }

    private boolean isReleased() {
        return this.hh == 0 && this.oo == 0;
    }

    private void startReleasing() {
        this.hh = 2;
        this.oo = 1;
        this.releasing = true;
    }

    private String getState() {
        return "H" + this.h + "O" + this.o;
    }


    public H2O() {
        
    }

    public void hydrogen(Runnable releaseHydrogen) throws InterruptedException {

        synchronized (this) {

            int hThr = ++this.h;
            String thrName = Thread.currentThread().getName() + ":-H" + hThr;
            System.out.println(thrName + " starting");
            while ((releasing && this.hh == 0) 
                    || (!releasing && !isReadyToRelease())) {
                System.out.println(thrName + " waiting for O " 
                    + (releasing ? "release" : "input")
                    + " at " + getState());
                wait();
            }
            // either releasing or ready for it
            if (isReadyToRelease()) { // isReadyToRelease
                if (!releasing) {
                    System.out.println(thrName + ": "+ getState() + " starting release");
                    startReleasing();
                }
            }

            System.out.println(thrName + ": H");
            // releaseHydrogen.run() outputs "H". Do not change or remove this line.
            releaseHydrogen.run();
            this.h--;
            this.hh--;

            if (isReleased()) { // fully released
                System.out.println(thrName + " done releasing; " + getState());
                this.releasing = false;
            }

            // yield control and wake up someone else
            System.out.println(thrName + " yielding at " + getState());
            notifyAll();
        }
    }

    public void oxygen(Runnable releaseOxygen) throws InterruptedException {

        synchronized (this) { // Thr3

            int oThr = ++this.o;
            String thrName = Thread.currentThread().getName() + ":-O" + oThr;
            System.out.println(thrName + " starting");
            while ((releasing && this.oo == 0) || (!releasing && !isReadyToRelease())) {
                System.out.println(thrName + " waiting for H " 
                    + (releasing ? "release" : "input")
                    + " at " + getState());
                wait();
            }
            // releasing || (h >= 2 && o >= 1)
            if (isReadyToRelease()) { // ready to start releasing
                if (!releasing) {
                    System.out.println(thrName + ": "+ getState() + " starting release");
                    startReleasing();
                }
            }

            System.out.println(thrName + ": O");
            // releaseOxygen.run() outputs "O". Do not change or remove this line.
            releaseOxygen.run();
            this.o--; // h = 2, o = true
            this.oo--;
            
            if (isReleased()) { // fully released
                System.out.println(thrName + " done releasing; " + getState());
                this.releasing = false;
            }

            System.out.println(thrName + " yielding at " + getState());
            // yield control and wake up someone else
            notifyAll(); // dies and notifies the others
        }
    }

}
