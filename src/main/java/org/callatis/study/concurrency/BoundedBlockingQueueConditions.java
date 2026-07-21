package org.callatis.study.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBlockingQueueConditions implements BoundedBlockingQueue {
    private final int[] elems;

    private int n = 0, start = 0, end = 0;

    private final Lock lock = new ReentrantLock();

    private final Condition empty = lock.newCondition();

    private final Condition full = lock.newCondition();

    public BoundedBlockingQueueConditions(int capacity) {
        this.elems = new int[capacity];
    }

    @Override
    public void enqueue(int element) throws InterruptedException {
        this.lock.lock();
        try {
            while (this.n == this.elems.length) {
                this.full.await();
            }
            this.elems[end] = element;
            this.n++;
            this.end = (this.end + 1) % this.elems.length;
            this.empty.signal();
        } finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public int dequeue() throws InterruptedException {
        this.lock.lock();
        try {
            while (this.n == 0) {
                this.empty.await();
            }
            int removed = this.elems[this.start];
            this.start = (this.start + 1) % this.elems.length;
            this.n--;
            this.full.signal();

            return removed;
        } finally {
            this.lock.unlock();
        }

    }
    
    @Override
    public int size() {
        this.lock.lock();
        try {
            return this.n;
        } finally {
            this.lock.unlock();
        }
    }

}
