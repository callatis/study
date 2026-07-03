package org.callatis.study.concurrency;

public interface BlockingQueue {
    void enqueue(int element) throws InterruptedException;

    int dequeue() throws InterruptedException;

    int size();
}