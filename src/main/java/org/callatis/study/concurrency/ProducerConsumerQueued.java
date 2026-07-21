package org.callatis.study.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ProducerConsumerQueued<T> implements ProducerConsumer<T> {

    private final int capacity;

    private final BlockingQueue<T> q;

    public ProducerConsumerQueued(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
        this.capacity = capacity;
        this.q = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getQSize() {
        return this.q.size();
    }

    @Override
    public void add(T item) throws InterruptedException {
        this.q.put(item);
    }

    @Override
    public T remove() throws InterruptedException {
        return this.q.take();
    }
}