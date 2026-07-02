package org.callatis.study.concurrency;

import java.util.function.IntConsumer;

public class ZeroEvenOdd {

    private final int n;

    private boolean zero = true;

    private int i = 0;
    
    public ZeroEvenOdd(int n) {
        this.n = n;
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void zero(IntConsumer printNumber) throws InterruptedException {
        synchronized (this) {
            do {
                while (!this.zero) {
                    this.wait();
                }
                if (this.i < n) {
                    System.out.println("0 (zero = " + (this.zero ? "ON" : "OFF") + ", i = " + this.i + ")");
                    printNumber.accept(0);
                }
                this.zero = false;
                this.notifyAll();
            } while (this.i < n);
        }
    }

    public void even(IntConsumer printNumber) throws InterruptedException {
        synchronized (this) {
            do {
                while (this.zero || (this.i %2 == 0)) {
                    this.wait();
                }
                if (this.i < n) {
                    System.out.println((this.i + 1) + " (zero = " + (this.zero ? "ON" : "OFF") + ")");
                    printNumber.accept(++this.i);
                }
                this.zero = true;
                this.notifyAll();
            } while (this.i < n);
        }
    }

    public void odd(IntConsumer printNumber) throws InterruptedException {
        synchronized (this) {
            do {
                while (this.zero || (this.i %2 > 0)) {
                    this.wait();
                }
                if (this.i < n) {
                    System.out.println((this.i + 1) + " (zero = " + (this.zero ? "ON" : "OFF") + ")");
                    printNumber.accept(++this.i);
                }
                this.zero = true;
                this.notifyAll();
            } while (this.i < n);
        }
    }
    
}