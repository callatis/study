package org.callatis.study.concurrency;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerFancy<T> implements ProducerConsumer<T> {

    private final int capacity;

    private final Queue<T> q;

    private int n;

    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public ProducerConsumerFancy(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
        this.capacity = capacity;
        this.q = new LinkedList<>();
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getQSize() {
        lock.lock();
        try {
            return this.n;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void add(T item) throws InterruptedException {
        lock.lock();
        try {
            while (n >= this.capacity) {
                notFull.await();
            }
            this.n++;
            q.add(item);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T remove() throws InterruptedException {
        T removed;
        lock.lock();
        try {
            while (n <= 0) {
                notEmpty.await();
            }
            this.n--;
            removed = q.remove();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
        return removed;
    }
}