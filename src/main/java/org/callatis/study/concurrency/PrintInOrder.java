package org.callatis.study.concurrency;

public class PrintInOrder {
    private int counter = 0;

    public PrintInOrder() {
        
    }

    public void first(Runnable printFirst) throws InterruptedException {
        
        synchronized (this) {
            // printFirst.run() outputs "first". Do not change or remove this line.
            printFirst.run();
            this.counter++;
            this.notifyAll();
        }
    }

    public void second(Runnable printSecond) throws InterruptedException {
        
        synchronized (this) {
            while (this.counter < 1) {
                this.wait();
            }
            // printSecond.run() outputs "second". Do not change or remove this line.
            printSecond.run();
            this.counter++;
            this.notifyAll();
        }
    }

    public void third(Runnable printThird) throws InterruptedException {
        
        synchronized (this) {
            while (this.counter < 2) {
                this.wait();
            }
            // printThird.run() outputs "third". Do not change or remove this line.
            printThird.run();
            this.counter++;
            this.notifyAll();
        }
    }

}