package org.callatis.study.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;

public class FizzBuzzOptimized {

    public FizzBuzzOptimized(int n) {
        this.n = n;
    }

    private final int n;
    private int k = 1;

    private final Lock lock = new ReentrantLock();
    private final Condition fizzCond = lock.newCondition();
    private final Condition buzzCond = lock.newCondition();
    private final Condition fizzBuzzCond = lock.newCondition();
    private final Condition intCond = lock.newCondition();

    // printFizz.run() outputs "fizz".
    public void fizz(Runnable printFizz) throws InterruptedException {
        while (true) {
            this.lock.lock();
            try {
                while (this.k <= n && (this.k % 3 != 0 || this.k % 5 == 0)) {
                    System.out.println("Fizz waiting at " + this.k);
                    this.fizzCond.await();
                }
                if (this.k > n) {
                    System.out.println("---- Fizz DONE! " + this.k + " > " + this.n);
                    // wake up whoever's still around
                    this.buzzCond.signal();
                    this.fizzBuzzCond.signal();
                    this.intCond.signal();

                    break;
                } 
                System.out.println("FIZZ " + this.k);
                printFizz.run();
                this.k++;

                // if we just processed a Fizz, next number is not divisible with 3
                Condition cond = (this.k % 5 == 0) ? this.buzzCond : this.intCond;
                cond.signal();
            } finally {
                this.lock.unlock();
            }
        }
    }

    // printBuzz.run() outputs "buzz".
    public void buzz(Runnable printBuzz) throws InterruptedException {
        while (true) {
            this.lock.lock();
            try {
                while (this.k <= n && (this.k % 5 != 0 || this.k % 3 == 0)) {
                    System.out.println("Buzz waiting at " + this.k);
                    this.buzzCond.await();
                }
                if (this.k > n) {
                    System.out.println("---- Buzz DONE! " + this.k + " > " + this.n);
                    // wake up whoever's still around
                    this.fizzCond.signal();
                    this.fizzBuzzCond.signal();
                    this.intCond.signal();

                    break;
                } 
                System.out.println("BUZZ " + this.k);
                printBuzz.run();
                this.k++;

                // if we just processed a Buzz, next number is not divisible with 5
                Condition cond = (this.k % 3 == 0) ? this.fizzCond : this.intCond;
                cond.signal();
            } finally {
                this.lock.unlock();
            }
        }
    }

    // printFizzBuzz.run() outputs "fizzbuzz".
    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        while (true) {
            this.lock.lock();
            try {
                while (this.k <= n && (this.k % 3 != 0 || this.k % 5 != 0)) {
                    System.out.println("FizzBuzz waiting at " + this.k);
                    this.fizzBuzzCond.await();
                }
                if (this.k > n) {
                    System.out.println("---- FizzBuzz DONE! " + this.k + " > " + this.n);
                    // wake up whoever's still around
                    this.buzzCond.signal();
                    this.fizzCond.signal();
                    this.intCond.signal();

                    break;
                }
                System.out.println("FIZZ-BUZZ " + this.k);
                printFizzBuzz.run();
                this.k++;
                // if we just processed a FizzBuzz, next number is divisible with neither 3 nor 5
                this.intCond.signal();
            } finally {
                this.lock.unlock();
            }
        }
    }

    // printNumber.accept(x) outputs "x", where x is an integer.
    public void number(IntConsumer printNumber) throws InterruptedException {
        while (true) {
            this.lock.lock();
            try {
                while (this.k <= n && (this.k % 3 == 0 || this.k % 5 == 0)) {
                    System.out.println("Number waiting at " + this.k);
                    this.intCond.await();
                }
                if (this.k > n) {
                    System.out.println("---- Number DONE! " + this.k + " > " + this.n);
                    // wake up whoever's still around
                    this.fizzCond.signal();
                    this.buzzCond.signal();
                    this.fizzBuzzCond.signal();

                    break;
                } 
                System.out.println("ACCEPT " + this.k);
                printNumber.accept(this.k++);

                // signal next
                if (this.k % 3 == 0) {
                     if (this.k % 5 == 0) {
                        System.out.println("Signal FizzBuzz");
                        this.fizzBuzzCond.signal();
                      } else {
                        System.out.println("Signal Fizz");
                        this.fizzCond.signal();
                      }
                } else if (this.k % 5 == 0) {
                    System.out.println("Signal Buzz");
                    this.buzzCond.signal();
                } else {
                    System.out.println("Signal nobody - still number");
                }
            } finally {
                this.lock.unlock();
            }
        }
    }

}
