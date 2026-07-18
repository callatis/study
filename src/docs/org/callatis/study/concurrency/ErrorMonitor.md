# Design an Error Monitor

**Difficulty:** Hard
**Topics:** Concurrency, Design, Ring Buffer, Lock-Free, Bit Manipulation

---

## Problem

You are asked to design an in-memory `ErrorMonitor` that tracks the error rate of an API.

Every time the API finishes handling a call, the serving thread reports the outcome:

```java
public void record(boolean isError)
```

Separately, a background component (the *introspector*) periodically asks for the error rate over the recent past, so it can persist it, graph it, or fire an alert:

```java
public double getErrorRate(long timeWindowSec)
```

`getErrorRate(w)` returns

```
errors_in_window / (successes_in_window + errors_in_window)
```

over the last `w` seconds. If no calls were recorded in the window, return `0.0`.

You do **not** need to implement the API being monitored or the introspector — only the two methods above.

### Window semantics

Time is divided into **wall-clock–aligned one-second buckets** (boundaries at `.000 ms`). A call recorded at `12:00:03.997` and one recorded at `12:00:03.002` belong to the same bucket.

`getErrorRate(w)` aggregates the `w` **completed** buckets preceding the current one. It does not include the second currently in flight. This means the reported rate lags reality by up to one second, which is acceptable.

### Concurrency requirements

- `record` is called by a **very large number of threads** — this is the hot path. It must not block, must not allocate, and must not become a contention point. Assume tens of millions of calls per second across the fleet of threads.
- `getErrorRate` may be called **concurrently by multiple introspector threads**, and concurrently with `record`.
- Memory usage must be bounded and independent of the call volume. You may **not** keep a log of individual events.
- `getErrorRate` reads the buckets one at a time while writers keep running, so a call recorded during the scan may or may not make it into the total. That's fine. What is not fine is a bucket's counts disagreeing with the second they belong to: the result must never include events from an expired window, and must never report more errors than total calls.

### Constraints

- `1 <= timeWindowSec <= 300`
- `record` and `getErrorRate` use the system wall clock internally; no clock is injected.
- The monitor must survive being idle for hours and then queried without reporting stale data.

---

## Examples

**Example 1**

```
t = 0.100s   record(false)
t = 0.400s   record(true)
t = 0.900s   record(false)
t = 1.500s   getErrorRate(1)   ->  0.3333333333333333
```
The window is the single completed bucket `[0s, 1s)`, which holds 2 successes and 1 error.

**Example 2**

```
t = 0.100s   record(true)
t = 0.200s   record(true)
             ... no traffic for 10 seconds ...
t = 11.500s  getErrorRate(5)   ->  0.0
```
The window `[6s, 11s)` contains no calls at all. Return `0.0`, **not** the two errors from bucket 0 — those must have aged out.

**Example 3**

```
t = 0.100s   record(true)
t = 1.100s   record(false)
t = 2.100s   record(false)
t = 3.500s   getErrorRate(2)   ->  0.0
t = 3.500s   getErrorRate(3)   ->  0.3333333333333333
```
The 2-second window covers buckets 1 and 2 (two successes). The 3-second window additionally covers bucket 0.

---

## Why the obvious approaches fail

| Approach | Problem |
| --- | --- |
| `ConcurrentLinkedQueue` of timestamped events | Unbounded memory; the queue head becomes a contention point; `getErrorRate` is O(events). |
| Two global `AtomicLong`s reset on each read | Only supports one window size; resetting races with concurrent `record`, losing samples; a second reader sees zeros. |
| `synchronized` around a ring of counters | Correct, but serializes every API call in the process. This is the thing the question exists to rule out. |
| Ring of `LongAdder` pairs, cleared when the bucket is reused | The clear is not atomic with respect to concurrent increments — a writer that read the bucket index before the clear will add into a bucket that has just been rewound, attributing old traffic to the new second. This is the subtle bug the interviewer is fishing for. |

---

## Follow-ups

1. Make `record` complete in a single atomic operation on the common path.
2. Eliminate the need for any background sweeper thread or scheduled cleanup — buckets must expire lazily.
3. Ensure a bucket that received no traffic is distinguishable from a bucket whose traffic has expired, without writing to it.
4. Reduce cache-line contention between writer threads.
5. What happens if the system clock steps backwards (NTP correction)?
6. What if a single bucket can overflow its counter width?

---

## Hints

<details>
<summary>Hint 1</summary>

Size the ring larger than the maximum window so that the bucket currently being written is never one of the buckets being read.
</details>

<details>
<summary>Hint 2</summary>

Store the bucket's *identity* alongside its counts, so a reader can tell whether the data it found actually belongs to the second it is asking about. If it doesn't, the bucket is stale — treat it as empty and move on. No clearing required.
</details>

<details>
<summary>Hint 3</summary>

Identity and counts must be read and written **together**. If they live in two separate fields, you have reintroduced the race. Pack them into one 64-bit word and use a single CAS.
</details>

<details>
<summary>Hint 4</summary>

To kill contention, give each bucket *S* independent cells — one per stripe — and have each thread write to a fixed stripe. Readers sum across stripes. This is what `LongAdder` does internally, applied per time bucket.
</details>

---

## Reference solution

The design: a **striped ring of packed cells**. Each cell is a single `long` holding a generation stamp plus a success and an error counter.

```
 63                    44 43                22 21                 0
+------------------------+--------------------+--------------------+
|      generation (20)   |   successes (22)   |    errors (22)     |
+------------------------+--------------------+--------------------+
```

Bucket index is `sec % BUCKETS`; generation is `sec / BUCKETS`. Two seconds that map to the same index always differ in generation, so the generation field is exactly the "which second does this data belong to" tag a reader needs.

- **Lazy reset.** A writer that finds an older generation in its cell overwrites the whole word — stamp and counts — with a fresh one in the same CAS. Expiry costs nothing and needs no sweeper.
- **Skipping.** A reader that finds a generation other than the one it wants skips the cell. That covers both "this second had no traffic on this stripe" and "this data is ancient", with no write on the read path.
- **Striping.** `STRIPES` independent cells per bucket, indexed by a stable per-thread id. Cells for the same bucket are `BUCKETS * 8` bytes apart, so concurrent writers at the same instant never share a cache line.

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLongArray;

public final class ErrorMonitor {

    /** Longest window getErrorRate will honour, in seconds. */
    public static final int MAX_WINDOW_SEC = 300;

    private static final int BUCKET_SHIFT = 9;
    private static final int BUCKETS      = 1 << BUCKET_SHIFT;   // 512 > MAX_WINDOW_SEC + 1
    private static final int BUCKET_MASK  = BUCKETS - 1;

    private static final int STRIPES     = 64;                   // power of two
    private static final int STRIPE_MASK = STRIPES - 1;

    private static final int  ERR_BITS   = 22;
    private static final int  OK_BITS    = 22;
    private static final int  GEN_SHIFT  = ERR_BITS + OK_BITS;   // 44
    private static final long COUNT_MASK = (1L << ERR_BITS) - 1; // 4_194_303
    private static final long ERR_ONE    = 1L;
    private static final long OK_ONE     = 1L << ERR_BITS;

    /** cells[stripe * BUCKETS + bucket] */
    private final AtomicLongArray cells = new AtomicLongArray(STRIPES * BUCKETS);

    private static final AtomicInteger STRIPE_SEQ = new AtomicInteger();
    private static final ThreadLocal<Integer> MY_STRIPE =
            ThreadLocal.withInitial(() -> STRIPE_SEQ.getAndIncrement() & STRIPE_MASK);

    private static long nowSec() {
        return System.currentTimeMillis() / 1000L;
    }

    public void record(boolean isError) {
        final long sec   = nowSec();
        final long gen   = sec >>> BUCKET_SHIFT;
        final int  idx   = MY_STRIPE.get() * BUCKETS + (int) (sec & BUCKET_MASK);
        final long delta = isError ? ERR_ONE : OK_ONE;
        final long fresh = (gen << GEN_SHIFT) | delta;

        for (;;) {
            final long cur    = cells.get(idx);
            final long curGen = cur >>> GEN_SHIFT;
            final long next;

            if (curGen == gen) {
                // Saturate rather than let a counter carry into its neighbour.
                long field = isError ? (cur & COUNT_MASK)
                                     : ((cur >>> ERR_BITS) & COUNT_MASK);
                if (field == COUNT_MASK) return;
                next = cur + delta;
            } else if (curGen < gen) {
                next = fresh;          // lazy reset of an expired bucket
            } else {
                return;                // clock stepped backwards; drop the sample
            }

            if (cells.compareAndSet(idx, cur, next)) return;
        }
    }

    public double getErrorRate(long timeWindowSec) {
        if (timeWindowSec <= 0) return 0.0;
        final int  window = (int) Math.min(timeWindowSec, MAX_WINDOW_SEC);
        final long now    = nowSec();

        long ok = 0L, err = 0L;

        for (long sec = now - window; sec < now; sec++) {
            final long gen    = sec >>> BUCKET_SHIFT;
            final int  bucket = (int) (sec & BUCKET_MASK);

            for (int s = 0; s < STRIPES; s++) {
                final long cell = cells.get(s * BUCKETS + bucket);
                if ((cell >>> GEN_SHIFT) != gen) continue;   // empty or expired
                ok  += (cell >>> ERR_BITS) & COUNT_MASK;
                err +=  cell & COUNT_MASK;
            }
        }

        final long total = ok + err;
        return total == 0L ? 0.0 : (double) err / (double) total;
    }
}
```

### Complexity

- `record` — O(1) time, zero allocation, one CAS on the uncontended path. Wait-free apart from the CAS retry loop, which only spins against writers on the same stripe *and* same second.
- `getErrorRate` — O(window × STRIPES) reads; at most `300 × 64 = 19,200` volatile loads. No writes, no locks, so introspectors never interfere with each other or with `record`.
- Memory — `STRIPES × BUCKETS × 8 B = 256 KB`, fixed, regardless of traffic.

### Design notes worth raising aloud

- **Why the generation lives in the same word as the counts.** Splitting them into `AtomicLong stamp` + `LongAdder ok/err` is the natural first draft and it is wrong: a writer can read a matching stamp, be descheduled for a second, and then increment counters that a reader has already begun attributing to the new bucket. One word, one CAS, no window.
- **Counter width.** 22 bits caps a bucket at ~4.19M events per second *per stripe* — 268M/s across 64 stripes. Saturating on overflow biases the rate slightly but never corrupts it. If that ceiling is too low, trade generation bits for count bits (16-bit generation still gives ~1000 years at 512 buckets), or widen a cell to two words guarded by a seqlock.
- **Snapshot consistency.** The scan is not atomic. A call recorded while the loop is halfway through may or may not appear. For a monitoring signal that is the right trade; making it atomic would require quiescing writers, which is precisely what we refused to do.
- **Clock steps.** Backwards jumps drop samples for the duration of the jump. Forwards jumps make buckets appear empty, which reads as `0.0`. `System.nanoTime()` would be monotonic but has no relationship to wall-clock second boundaries, which the aligned-bucket requirement demands.
- **Reader/writer race on the boundary.** Excluding the in-flight second means a reader never touches a bucket that a writer is actively resetting for a *new* generation, so a stale bucket can never be counted as fresh.

---

## Test cases

```java
import org.junit.jupiter.api.Test;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import static org.junit.jupiter.api.Assertions.*;

class ErrorMonitorTest {

    /** Block until just after the next wall-clock second boundary. */
    private static void alignToSecond() throws InterruptedException {
        long ms = System.currentTimeMillis();
        Thread.sleep(1000 - (ms % 1000) + 20);
    }

    @Test
    void emptyWindowReturnsZero() {
        assertEquals(0.0, new ErrorMonitor().getErrorRate(5));
    }

    @Test
    void singleBucketRate() throws Exception {
        ErrorMonitor m = new ErrorMonitor();
        alignToSecond();
        m.record(false);
        m.record(true);
        m.record(false);
        Thread.sleep(1000);
        assertEquals(1.0 / 3.0, m.getErrorRate(1), 1e-9);
    }

    @Test
    void currentInFlightSecondIsExcluded() throws Exception {
        ErrorMonitor m = new ErrorMonitor();
        alignToSecond();
        m.record(true);
        assertEquals(0.0, m.getErrorRate(1));   // still inside the same bucket
    }

    @Test
    void dataExpiresFromTheWindow() throws Exception {
        ErrorMonitor m = new ErrorMonitor();
        alignToSecond();
        m.record(true);
        Thread.sleep(3000);
        assertEquals(1.0, m.getErrorRate(5), 1e-9);   // still in a 5s window
        assertEquals(0.0, m.getErrorRate(1), 1e-9);   // aged out of a 1s window
    }

    @Test
    void idleGapsAreSkippedNotCounted() throws Exception {
        ErrorMonitor m = new ErrorMonitor();
        alignToSecond();
        m.record(true);
        Thread.sleep(2000);
        m.record(false);
        Thread.sleep(1000);
        assertEquals(0.5, m.getErrorRate(5), 1e-9);   // one error, one success, gap ignored
    }

    @Test
    void concurrentWritersLoseNothing() throws Exception {
        ErrorMonitor m = new ErrorMonitor();
        int threads = 32, perThread = 50_000;
        LongAdder expectedErrors = new LongAdder();

        alignToSecond();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch done = new CountDownLatch(threads);
        for (int t = 0; t < threads; t++) {
            final int seed = t;
            pool.execute(() -> {
                for (int i = 0; i < perThread; i++) {
                    boolean err = ((i + seed) % 4 == 0);
                    if (err) expectedErrors.increment();
                    m.record(err);
                }
                done.countDown();
            });
        }
        assertTrue(done.await(30, TimeUnit.SECONDS));
        pool.shutdown();

        Thread.sleep(1200);
        double expected = expectedErrors.sum() / (double) (threads * (long) perThread);
        assertEquals(expected, m.getErrorRate(60), 1e-6);
    }

    @Test
    void concurrentReadersDoNotDisturbEachOther() throws Exception {
        ErrorMonitor m = new ErrorMonitor();
        alignToSecond();
        for (int i = 0; i < 1000; i++) m.record(i % 5 == 0);
        Thread.sleep(1100);

        ExecutorService pool = Executors.newFixedThreadPool(8);
        Future<?>[] fs = new Future<?>[8];
        for (int i = 0; i < 8; i++) {
            fs[i] = pool.submit(() -> {
                for (int k = 0; k < 200; k++) {
                    assertEquals(0.2, m.getErrorRate(10), 1e-9);
                }
            });
        }
        for (Future<?> f : fs) f.get(30, TimeUnit.SECONDS);
        pool.shutdown();
    }

    @Test
    void windowLongerThanMaxIsClamped() {
        ErrorMonitor m = new ErrorMonitor();
        assertEquals(0.0, m.getErrorRate(100_000));   // must not read past the ring
    }
}
```

> The timing-dependent tests above are honest about what the real class does but are flaky by nature. In a production repo, extract `nowSec()` behind a package-private seam so the tests can drive time deterministically — then mention in the interview that you deliberately kept it out of the public API.
