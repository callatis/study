package org.callatis.study.concurrency;

import java.util.concurrent.Semaphore;

public class BoundedBlockingQueueSemaphores implements BoundedBlockingQueue {

    private final int[] elems;

    private int n = 0, start = 0, end = 0;

    private final Semaphore emptySem;

    private final Semaphore fullSem;

    public BoundedBlockingQueueSemaphores(int capacity) {
        this.elems = new int[capacity];
        this.emptySem = new Semaphore(capacity);
        this.fullSem = new Semaphore(0);
    }

    @Override
    public void enqueue(int element) throws InterruptedException {
        this.emptySem.acquire();
        synchronized (this) {
            this.elems[end] = element;
            this.n++;
            this.end = (this.end + 1) % this.elems.length;

            this.fullSem.release();
        }
    }
    
    @Override
    public int dequeue() throws InterruptedException {
        this.fullSem.acquire();
        synchronized (this) {
            int removed = this.elems[this.start];
            this.start = (this.start + 1) % this.elems.length;
            this.n--;
            this.emptySem.release();

            return removed;
        }

    }
    
    @Override
    public synchronized int size() {
        return this.n;
    }

}
