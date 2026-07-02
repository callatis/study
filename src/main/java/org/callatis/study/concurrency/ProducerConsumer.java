package org.callatis.study.concurrency;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerConsumer<T> {

    public static class Producer {

        private final ProducerConsumer<Integer> pc;
        
        private final int n;

        private final long delay;

        public Producer(ProducerConsumer<Integer> pc, int n, long delay) {
            this.pc = pc;
            this.n = n;
            this.delay = delay;
        }

        public void produce() throws InterruptedException {
            for (int i = 0; i < this.n; i++) {
                this.pc.add(i);
                System.out.println("Added " + i);
                if (this.delay > 0) {
                    Thread.sleep(this.delay);
                }
            }
        }
    }

    public static class Consumer {

        private final int n;

        private final long delay;

        private final ProducerConsumer<Integer> pc;
        
        public Consumer(ProducerConsumer<Integer> pc, int n, int delay) {
            this.pc = pc;
            this.n = n;
            this.delay = delay;
        }

        public void consume() throws InterruptedException {
            for (int i = 0; i < n; i++) { 
                int removed = pc.remove();
                System.out.println("Removed " + removed);
                if (this.delay > 0) {
                    Thread.sleep(this.delay);
                }
            }
        }
    }

    private final int capacity;

    private final Queue<T> q;

    private int n;

    public ProducerConsumer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be greater than zero");
        }
        this.capacity = capacity;
        this.q = new LinkedList<>();
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getQSize() {
        synchronized (this) {
            return this.n;
        }
    }

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