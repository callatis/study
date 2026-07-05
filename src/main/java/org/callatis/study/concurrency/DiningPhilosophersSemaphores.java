package org.callatis.study.concurrency;

import java.util.concurrent.Semaphore;

public class DiningPhilosophersSemaphores implements DiningPhilosophers {

    private final Semaphore totalSem = new Semaphore(4, true);
    private final Semaphore[] pSems = new Semaphore[] {
        new Semaphore(1, true),
        new Semaphore(1, true),
        new Semaphore(1, true),
        new Semaphore(1, true),
        new Semaphore(1, true)
    };
    private final Semaphore[] sems = new Semaphore[] {
        new Semaphore(1, true),
        new Semaphore(1, true),
        new Semaphore(1, true),
        new Semaphore(1, true),
        new Semaphore(1, true)
    };

    @Override
    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {

        this.pSems[philosopher].acquire();
        this.totalSem.acquire();
        int leftFork = philosopher;
        int rightFork = (philosopher + 1) % 5;

        if ((philosopher & 1) == 0) {
            this.sems[leftFork].acquire();
            this.sems[rightFork].acquire();
        } else {
            this.sems[rightFork].acquire();
            this.sems[leftFork].acquire();
        }

        pickLeftFork.run();
        pickRightFork.run();
        eat.run();
        putLeftFork.run();
        putRightFork.run();

        this.sems[rightFork].release();
        this.sems[leftFork].release();

        this.totalSem.release();
        this.pSems[philosopher].release();
    }
}
