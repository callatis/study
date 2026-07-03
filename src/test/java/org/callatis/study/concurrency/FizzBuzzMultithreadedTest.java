package org.callatis.study.concurrency;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.IntConsumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class FizzBuzzMultithreadedTest {

    private static final long THREAD_JOIN_TIMEOUT_MS = 2000L;

    private final int n;
    private final String expectedOutput;

    public FizzBuzzMultithreadedTest(int n, String expectedOutput) {
        this.n = n;
        this.expectedOutput = expectedOutput;
    }

    @Parameters(name = "Example {0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // From Fizz Buzz Multithreaded.md examples.
            {15, "1,2,fizz,4,buzz,fizz,7,8,fizz,buzz,11,fizz,13,14,fizzbuzz"},
            {5, "1,2,fizz,4,buzz"}
        });
    }

    @Test
    public void testFizzBuzzMultithreaded() throws InterruptedException {
        FizzBuzzMultithreaded fizzBuzz = new FizzBuzzMultithreaded(n);
        @SuppressWarnings("java:S1149")
        final StringBuffer output = new StringBuffer();
        AtomicReference<Throwable> threadError = new AtomicReference<>();

        Runnable printFizz = () -> appendToken(output, "fizz");
        Runnable printBuzz = () -> appendToken(output, "buzz");
        Runnable printFizzBuzz = () -> appendToken(output, "fizzbuzz");
        IntConsumer printNumber = value -> appendToken(output, String.valueOf(value));

        Thread fizzThread = new Thread(() -> runSafely(() -> fizzBuzz.fizz(printFizz), threadError), "fizz-thread");
        Thread buzzThread = new Thread(() -> runSafely(() -> fizzBuzz.buzz(printBuzz), threadError), "buzz-thread");
        Thread fizzBuzzThread = new Thread(() -> runSafely(() -> fizzBuzz.fizzbuzz(printFizzBuzz), threadError), "fizzbuzz-thread");
        Thread numberThread = new Thread(() -> runSafely(() -> fizzBuzz.number(printNumber), threadError), "number-thread");

        fizzThread.start();
        buzzThread.start();
        fizzBuzzThread.start();
        numberThread.start();

        waitForCompletion(fizzThread, buzzThread, fizzBuzzThread, numberThread);

        if (threadError.get() != null) {
            Throwable thr = threadError.get();
            throw new AssertionError("Worker failed", thr);
        }

        assertEquals(expectedOutput, output.toString());
    }

    private static void waitForCompletion(
            Thread fizzThread,
            Thread buzzThread,
            Thread fizzBuzzThread,
            Thread numberThread) throws InterruptedException {
        fizzThread.join(THREAD_JOIN_TIMEOUT_MS);
        buzzThread.join(THREAD_JOIN_TIMEOUT_MS);
        fizzBuzzThread.join(THREAD_JOIN_TIMEOUT_MS);
        numberThread.join(THREAD_JOIN_TIMEOUT_MS);

        if (fizzThread.isAlive() || buzzThread.isAlive() || fizzBuzzThread.isAlive() || numberThread.isAlive()) {
            fizzThread.interrupt();
            buzzThread.interrupt();
            fizzBuzzThread.interrupt();
            numberThread.interrupt();
            fail("Threads did not complete in time. Potential deadlock.");
        }
    }

    private static void runSafely(ThrowingRunnable action, AtomicReference<Throwable> threadError) {
        try {
            action.run();
        } catch (InterruptedException t) {
            threadError.compareAndSet(null, t);
        }
    }

    private static void appendToken(StringBuffer output, String token) {
        synchronized (output) {
            if (output.length() > 0) {
                output.append(',');
            }
            output.append(token);
        }
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws InterruptedException;
    }
}