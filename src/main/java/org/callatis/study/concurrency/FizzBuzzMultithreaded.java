package org.callatis.study.concurrency;

import java.util.function.IntConsumer;

public class FizzBuzzMultithreaded {

    private final int n;

    private int k = 1;

    public FizzBuzzMultithreaded(int n) {
        this.n = n;
    }

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (this.k <= n && (this.k % 3 != 0 || this.k % 5 == 0)) {
                    System.out.println("Fizz waiting at " + this.k);
                    wait();
                }
                if (this.k > n) {
                    System.out.println("---- Fizz DONE! " + this.k + " > " + this.n);
                    notifyAll();
                    break;
                } 
                System.out.println("FIZZ " + this.k);
                printFizz.run();
                this.k++;
                notifyAll();
            }
        }
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (this.k <= n && (this.k % 5 != 0 || this.k % 3 == 0)) {
                    System.out.println("Buzz waiting at " + this.k);
                    wait();
                }
                if (this.k > n) {
                    System.out.println("---- Buzz DONE! " + this.k + " > " + this.n);
                    notifyAll();
                    break;
                } 
                System.out.println("BUZZ " + this.k);
                printBuzz.run();
                this.k++;
                notifyAll();
            }
        }
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (this.k <= n && (this.k % 3 != 0 || this.k % 5 != 0)) {
                    System.out.println("FizzBuzz waiting at " + this.k);
                    wait();
                }
                if (this.k > n) {
                    System.out.println("---- FizzBuzz DONE! " + this.k + " > " + this.n);
                    notifyAll();
                    break;
                }
                System.out.println("FIZZ-BUZZ " + this.k);
                printFizzBuzz.run();
                this.k++;
                notifyAll();
            }
        }
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            synchronized (this) {
                while (this.k <= n && (this.k % 3 == 0 || this.k % 5 == 0)) {
                    System.out.println("Number waiting at " + this.k);
                    wait();
                }
                if (this.k > n) {
                    System.out.println("---- Number DONE! " + this.k + " > " + this.n);
                    notifyAll();
                    break;
                } 
                System.out.println("ACCEPT " + this.k);
                printNumber.accept(this.k++);
                notifyAll();
            }
        }
    }

}
