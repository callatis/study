package org.callatis.study.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * Thread-safe metric registry with per-metric cardinality (series) limiting.
 *
 * A "series" is identified by (metricName, key). Each series has its own counter.
 * Each metric may hold at most {@code maxSeriesPerMetric} distinct keys; writes that
 * would create a new series beyond that budget are rejected at write time (admission
 * control) rather than at read time, so a cardinality explosion can never OOM us.
 */
public final class MetricRegistry {

    private final int maxSeriesPerMetric;
    private final ConcurrentHashMap<String, Metric> metrics = new ConcurrentHashMap<>();
    private final LongAdder droppedForCardinality = new LongAdder();

    public MetricRegistry(int maxSeriesPerMetric) {
        if (maxSeriesPerMetric <= 0) throw new IllegalArgumentException("limit must be > 0");
        this.maxSeriesPerMetric = maxSeriesPerMetric;
    }

    /**
     * Record {@code value} into the counter for (metricName, key).
     * @return true if accepted, false if rejected because the metric is at its series cap.
     */
    public boolean record(String metricName, String key, long value) {
        // computeIfAbsent bounds the number of *metric names*; the interesting cap
        // (keys per metric) is enforced inside Metric.record below.
        Metric metric = metrics.computeIfAbsent(metricName, n -> new Metric(maxSeriesPerMetric));
        boolean accepted = metric.record(key, value);
        if (!accepted) droppedForCardinality.increment();
        return accepted;
    }

    public long counterValue(String metricName, String key) {
        Metric m = metrics.get(metricName);
        if (m == null) return 0;
        LongAdder a = m.series.get(key);
        return a == null ? 0 : a.sum();
    }

    public int seriesCount(String metricName) {
        Metric m = metrics.get(metricName);
        return m == null ? 0 : m.series.size();
    }

    public long droppedForCardinality() { return droppedForCardinality.sum(); }

    /** One metric name and its bounded set of per-key counters. */
    private static final class Metric {
        final int limit;
        final ConcurrentHashMap<String, LongAdder> series = new ConcurrentHashMap<>();
        // Reservation counter: reserved slots >= actual distinct keys, and never exceeds limit.
        final AtomicInteger reserved = new AtomicInteger(0);

        Metric(int limit) { this.limit = limit; }

        boolean record(String key, long value) {
            // Fast path: series already exists. No contention on the reservation counter.
            LongAdder existing = series.get(key);
            if (existing != null) {
                existing.add(value);
                return true;
            }

            // Slow path: this looks like a new series. Atomically reserve a slot first.
            int cur;
            do {
                cur = reserved.get();
                if (cur >= limit) {
                    return false; // at cap: refuse the new series (existing ones keep working)
                }
            } while (!reserved.compareAndSet(cur, cur + 1));

            // Slot reserved. Try to publish the new counter.
            LongAdder created = new LongAdder();
            LongAdder prev = series.putIfAbsent(key, created);
            if (prev != null) {
                // Another thread created this same key concurrently: give our slot back.
                reserved.decrementAndGet();
                prev.add(value);
            } else {
                created.add(value);
            }
            return true;
        }
    }

    // ---- concurrency stress test / demo ----
    public static void main(String[] args) throws InterruptedException {
        final int LIMIT = 1_000;
        final int THREADS = 64;
        final int DISTINCT_KEYS_ATTEMPTED = 50_000; // way over the cap -> most rejected
        final MetricRegistry reg = new MetricRegistry(LIMIT);

        Thread[] pool = new Thread[THREADS];
        for (int t = 0; t < THREADS; t++) {
            pool[t] = new Thread(() -> {
                java.util.Random r = new java.util.Random();
                for (int i = 0; i < 2_000_000; i++) {
                    // Every thread hammers the SAME key space, so they race to create
                    // the same new series constantly -- the worst case for the cap.
                    int k = r.nextInt(DISTINCT_KEYS_ATTEMPTED);
                    reg.record("request_latency", "user_" + k, 1);
                }
            });
            pool[t].start();
        }
        for (Thread th : pool) th.join();

        int distinct = reg.seriesCount("request_latency");
        System.out.println("distinct series kept : " + distinct + "  (cap = " + LIMIT + ")");
        System.out.println("writes dropped       : " + reg.droppedForCardinality());
        System.out.println("cap respected        : " + (distinct <= LIMIT));

        // Correctness: total recorded across kept series + dropped == total attempts.
        long kept = 0;
        for (int k = 0; k < DISTINCT_KEYS_ATTEMPTED; k++) kept += reg.counterValue("request_latency", "user_" + k);
        long total = (long) THREADS * 2_000_000;
        System.out.println("accounted (kept+drop): " + (kept + reg.droppedForCardinality()) + "  (attempts = " + total + ")");
        System.out.println("no writes lost       : " + (kept + reg.droppedForCardinality() == total));
    }
}
