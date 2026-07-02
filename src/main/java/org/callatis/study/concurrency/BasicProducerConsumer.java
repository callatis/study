package org.callatis.study.concurrency;

import java.util.LinkedList;
import java.util.Queue;

public class BasicProducerConsumer<T> implements ProducerConsumer<T> {

    private final int capacity;

    private final Queue<T> q;

    private int n;

    public BasicProducerConsumer(int capacity) {
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
        synchronized (this) {
            return this.n;
        }
    }

    @Override
    public void add(T item) throws InterruptedException {
        synchronized (this) {
            while (n >= this.capacity) {
                this.wait();
            }
            this.n++;
            q.add(item);
            this.notifyAll();
        }
    }

    @Override
    public T remove() throws InterruptedException {
        T removed;
        synchronized (this) {
            while (n <= 0) {
                this.wait();
            }
            this.n--;
            removed = q.remove();
            this.notifyAll();
        }
        return removed;
    }
}