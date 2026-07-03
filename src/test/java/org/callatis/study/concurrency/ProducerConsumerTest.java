package org.callatis.study.concurrency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ProducerConsumerTest {

    private final String implementationType;
    private final String scenarioName;
    private final int producerCount;
    private final int producerItems;
    private final int producerDelayMs;
    private final int consumerCount;
    private final int consumerItems;
    private final int consumerDelayMs;

    public ProducerConsumerTest(
            String implementationType,
            String scenarioName,
            int producerCount,
            int producerItems,
            int producerDelayMs,
            int consumerCount,
            int consumerItems,
            int consumerDelayMs) {
        this.implementationType = implementationType;
        this.scenarioName = scenarioName;
        this.producerCount = producerCount;
        this.producerItems = producerItems;
        this.producerDelayMs = producerDelayMs;
        this.consumerCount = consumerCount;
        this.consumerItems = consumerItems;
        this.consumerDelayMs = consumerDelayMs;
    }

    @Parameters(name = "{0}-{1}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            // From Producer-Consumer.md examples.
            {"BasicProducerConsumer", "test1x10x0v1x10x0", 1, 10, 0, 1, 10, 0},
            {"BasicProducerConsumer", "test1x10x1000v1x10x1000", 1, 10, 1000, 1, 10, 1000},
            {"BasicProducerConsumer", "test2x10x1000v1x20x0", 2, 10, 1000, 1, 20, 0},
            {"BasicProducerConsumer", "test1x20x0v2x10x1000", 1, 20, 0, 2, 10, 1000},
            {"FancyProducerConsumer", "test1x10x0v1x10x0", 1, 10, 0, 1, 10, 0},
            {"FancyProducerConsumer", "test1x10x1000v1x10x1000", 1, 10, 1000, 1, 10, 1000},
            {"FancyProducerConsumer", "test2x10x1000v1x20x0", 2, 10, 1000, 1, 20, 0},
            {"FancyProducerConsumer", "test1x20x0v2x10x1000", 1, 20, 0, 2, 10, 1000},
            {"QueuedProducerConsumer", "test1x10x0v1x10x0", 1, 10, 0, 1, 10, 0},
            {"QueuedProducerConsumer", "test1x10x1000v1x10x1000", 1, 10, 1000, 1, 10, 1000},
            {"QueuedProducerConsumer", "test2x10x1000v1x20x0", 2, 10, 1000, 1, 20, 0},
            {"QueuedProducerConsumer", "test1x20x0v2x10x1000", 1, 20, 0, 2, 10, 1000},
            // {"BasicProducerConsumer", "test4x5x1000v2x10x500", 4, 5, 1000, 2, 10, 500},
            // {"BasicProducerConsumer", "test3x20x0v6x10x0", 3, 20, 0, 6, 10, 0},
            // {"BasicProducerConsumer", "test6x10x0v3x20x2000", 6, 10, 0, 3, 20, 2000},
            // {"BasicProducerConsumer", "test3x20x2000v6x10x0", 3, 20, 2000, 6, 10, 0},
            // {"BasicProducerConsumer", "test8x25x100v5x40x100", 8, 25, 100, 5, 40, 100},
            // {"BasicProducerConsumer", "test10x10x0v10x10x0", 10, 10, 0, 10, 10, 0},
            // {"BasicProducerConsumer", "test2x100x0v1x200x50", 2, 100, 0, 1, 200, 50},
            // {"BasicProducerConsumer", "test1x200x50v4x50x0", 1, 200, 50, 4, 50, 0},
            // {"BasicProducerConsumer", "test5x40x250v4x50x750", 5, 40, 250, 4, 50, 750},
            // {"BasicProducerConsumer", "test12x5x0v3x20x1500", 12, 5, 0, 3, 20, 1500},
            // {"BasicProducerConsumer", "test3x20x1500v12x5x0", 3, 20, 1500, 12, 5, 0},
            {"BasicProducerConsumer", "test16x50x10v20x40x10", 16, 50, 10, 20, 40, 10}
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsZeroCapacity() {
        ProducerConsumer<Integer> pc = createProducerConsumer(0);

        fail("Should have thrown but it created " + pc);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNegativeCapacity() {
        ProducerConsumer<Integer> pc = createProducerConsumer(-1);

        fail("Should have thrown but it created " + pc);
    }

    @Test
    public void testConstructorStoresCapacity() {
        ProducerConsumer<String> pc = createProducerConsumer(3);

        assertEquals(3, pc.getCapacity());
    }

    @Test
    public void testProducerConsumerScenario() throws InterruptedException {
        ProducerConsumer<Integer> pc = createProducerConsumer(Math.max(1, producerItems * producerCount));
        AtomicReference<Throwable> threadError = new AtomicReference<>();
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < producerCount; i++) {
            Producer p = new Producer(pc, producerItems, producerDelayMs);
            Thread producerThread = new Thread(() -> runProducer(p, threadError), scenarioName + "-P" + i);
            threads.add(producerThread);
        }

        for (int i = 0; i < consumerCount; i++) {
            Consumer c = new Consumer(pc, consumerItems, consumerDelayMs);
            Thread consumerThread = new Thread(() -> runConsumer(c, threadError), scenarioName + "-C" + i);
            threads.add(consumerThread);
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        if (threadError.get() != null) {
            Throwable thr = threadError.get();
            throw new AssertionError("Worker failed for " + implementationType + " in scenario " + scenarioName, thr);
        }

        assertEquals("Wrong number of items left for " + implementationType + " in scenario " + scenarioName, 0, pc.getQSize());
    }

    private <T> ProducerConsumer<T> createProducerConsumer(int capacity) {
        if ("BasicProducerConsumer".equals(implementationType)) {
            return new BasicProducerConsumer<>(capacity);
        } else if ("FancyProducerConsumer".equals(implementationType)) {
            return new FancyProducerConsumer<>(capacity);
        } else if ("QueuedProducerConsumer".equals(implementationType)) {
            return new QueuedProducerConsumer<>(capacity);
        }

        throw new IllegalArgumentException("Unsupported implementation type: " + implementationType);
    }

    private static void runProducer(Producer producer, AtomicReference<Throwable> threadError) {
        try {
            producer.produce();
        } catch (InterruptedException t) {
            threadError.compareAndSet(null, t);
        }
    }

    private static void runConsumer(Consumer consumer, AtomicReference<Throwable> threadError) {
        try {
            consumer.consume();
        } catch (InterruptedException t) {
            threadError.compareAndSet(null, t);
        }
    }

}