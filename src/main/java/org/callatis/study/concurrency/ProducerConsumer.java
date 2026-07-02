package org.callatis.study.concurrency;

public interface ProducerConsumer<T> {

    int getCapacity();

    int getQSize();

    void add(T item) throws InterruptedException;

    T remove() throws InterruptedException;
}