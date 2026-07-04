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
        int fork1;
        int fork2;
        Runnable pickFork1;
        Runnable pickFork2;
        Runnable putFork1;
        Runnable putFork2;
        boolean isEven = (philosopher % 2 == 0);
        if (isEven) { // even number, e.g. 0, 2, 4
            // start with right
            fork1 = philosopher;
            pickFork1 = pickRightFork;
            putFork1 = putRightFork;
            fork2 = (philosopher + 1) % 5;
            pickFork2 = pickLeftFork;
            putFork2 = putLeftFork;
        } else {
            fork1 = (philosopher + 1) % 5;
            pickFork1 = pickLeftFork;
            putFork1 = putLeftFork;
            fork2 = philosopher;
            pickFork2 = pickRightFork;
            putFork2 = putRightFork;
        }
        this.sems[fork1].acquire();
        pickFork1.run();
        System.out.println(philosopher + " picked " + (isEven ? "right" : "left") + " fork");
        this.sems[fork2].acquire();
        pickFork2.run();
        System.out.println(philosopher + " picked " + (isEven ? "left" : "right") + " fork");
        eat.run();
        System.out.println(philosopher + " ate");
        putFork2.run();
        System.out.println(philosopher + " dropped " + (isEven ? "left" : "right") + " fork");
        this.sems[fork2].release();
        putFork1.run();
        System.out.println(philosopher + " dropped " + (isEven ? "right" : "left") + " fork");
        this.sems[fork1].release();

        this.totalSem.release();
        this.pSems[philosopher].release();
    }
}
