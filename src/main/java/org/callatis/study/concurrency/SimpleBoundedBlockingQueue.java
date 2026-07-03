package org.callatis.study.concurrency;

public class SimpleBoundedBlockingQueue implements BlockingQueue {

    private final int[] elems;

    private int n = 0, start = 0, end = 0;

    public SimpleBoundedBlockingQueue(int capacity) {
        this.elems = new int[capacity];
    }

    @Override
    public void enqueue(int element) throws InterruptedException {
        synchronized (this) {
            while (this.n >= this.elems.length) {
                wait();
            }

            this.elems[end] = element;
            this.n++;
            this.end = (this.end + 1) % this.elems.length;

            this.notifyAll();
        }
    }
    
    @Override
    public int dequeue() throws InterruptedException {
        synchronized (this) {
            while (this.n == 0) {
                wait();
            }

            int removed = this.elems[this.start];
            this.start = (this.start + 1) % this.elems.length;
            this.n--;
            this.notifyAll();
            
            return removed;
        }

    }
    
    @Override
    public int size() {
        return this.n;
    }
}
