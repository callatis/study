package org.callatis.study.concurrency;

import java.util.function.IntConsumer;

public interface FizzBuzz {

    void fizz(Runnable printFizz) throws InterruptedException;

    void buzz(Runnable printBuzz) throws InterruptedException;

    void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException;

    void number(IntConsumer printNumber) throws InterruptedException;
}
