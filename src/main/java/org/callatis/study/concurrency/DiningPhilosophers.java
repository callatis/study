package org.callatis.study.concurrency;

public interface DiningPhilosophers {

    void wantsToEat(int philosopher,
                    Runnable pickLeftFork,
                    Runnable pickRightFork,
                    Runnable eat,
                    Runnable putLeftFork,
                    Runnable putRightFork) throws InterruptedException;
}
