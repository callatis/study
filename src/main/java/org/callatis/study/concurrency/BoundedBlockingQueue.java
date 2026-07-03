package org.callatis.study.concurrency;

public class BoundedBlockingQueue {

    private final int[] elems;

    private int n = 0, start = 0, end = 0;

    public BoundedBlockingQueue(int capacity) {
        this.elems = new int[capacity];
    }

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
    
    public int size() {
        return this.n;
    }
}
