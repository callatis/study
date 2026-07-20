# Rate Limiter Design Problems

A companion set to **Design a Token Bucket Rate Limiter** (separate file). Four more rate-limiting algorithms, each written LeetCode-style with concurrency as the core requirement rather than a footnote.

Because your goal is to drill Java concurrency, each problem below deliberately exercises a **different** concurrency skill. Pick based on what you want to practice:

| Problem | Difficulty | Concurrency skill it tests |
|---|---|---|
| Fixed Window Counter | Easy | Single-word atomics — packing state into one `AtomicLong` and CAS-ing it |
| Sliding Window Log | Medium | Guarding a **compound operation over a collection**; why lock-free is hard here; O(limit) ring buffer |
| Sliding Window Counter | Medium | CAS over a **richer immutable snapshot** with window-rollover logic |
| Leaky Bucket (queue) | Medium | **Producer/consumer** — bounded blocking buffer + a background draining thread |

Shared conventions for all four: a request "at time `t`" is inside a rolling window ending at `now` iff `now - t < windowMillis`. Use `System.nanoTime()` for elapsed-time math. The offline test-harness pattern from the Token Bucket file applies to every problem here — only the asserted invariant changes, and it's stated per problem.

---
---

# 1. Design a Fixed Window Counter Rate Limiter

**Difficulty:** Easy · **Topics:** Design, Concurrency, Atomic Operations · **Companies:** Stripe, Google

---

Divide time into consecutive, non-overlapping **windows** of fixed length. Each window has its own counter starting at zero. A request in the current window is admitted if the window's counter is below the limit, and it increments that counter. When the clock crosses into a new window, the counter resets to zero.

Windows are aligned to absolute time: the window index for an instant `now` is `now / windowMillis`.

Design the `FixedWindowCounter` class:

- `FixedWindowCounter(int maxRequests, long windowMillis)`
  Sets the limit of `maxRequests` admitted requests per window of `windowMillis` milliseconds.

- `boolean tryAcquire()`
  Determines the current window. If the clock has advanced into a new window since the last call, the count resets to zero first. If the current window's count is below `maxRequests`, admits the request (increments the count) and returns `true`; otherwise returns `false` without incrementing.

**Thread-safety requirement.** `tryAcquire` is called concurrently from many threads. The following invariant must hold for every interleaving:

> Within any single window `[k·W, (k+1)·W)`, the number of requests granted never exceeds `maxRequests`.

Two threads observing the same window must not both read count `maxRequests − 1` and both succeed (no lost updates), and the check-window-then-reset-then-increment sequence must be a single atomic step.

## Example 1

`FixedWindowCounter(3, 1000)` — 3 requests per 1-second window.

| Wall clock | Window | Call | Count before | Result | Count after |
|---|---|---|---|---|---|
| 900 ms | 0 | `tryAcquire()` | 0 | `true` | 1 |
| 950 ms | 0 | `tryAcquire()` | 1 | `true` | 2 |
| 999 ms | 0 | `tryAcquire()` | 2 | `true` | 3 |
| 999 ms | 0 | `tryAcquire()` | 3 | `false` | 3 |
| 1000 ms | 1 | `tryAcquire()` | 0 (reset) | `true` | 1 |
| 1050 ms | 1 | `tryAcquire()` | 1 | `true` | 2 |
| 1100 ms | 1 | `tryAcquire()` | 2 | `true` | 3 |

**Explanation.** Window 0 admits three requests and rejects the fourth. At 1000 ms the clock enters window 1, the counter resets, and three more are admitted. Note the algorithm's defining weakness: between 900 ms and 1100 ms — a 200 ms span — **six** requests were admitted, double the nominal rate of 3 per second. A burst clustered at the tail of one window and the head of the next slips through. This boundary burst is exactly what the sliding-window algorithms fix.

## Constraints

- `1 <= maxRequests <= 10^9`
- `1 <= windowMillis <= 10^9`
- Calls may be made concurrently from up to `64` threads.
- At most `10^6` total calls.

## Follow-ups

1. **One-word lock-free.** The entire state is `{windowIndex, count}`. Pack both into a single 64-bit value (e.g. high bits = window index, low bits = count) and implement `tryAcquire` as a single `AtomicLong.compareAndSet` retry loop — no locks, no `AtomicReference` allocation. What's the bit budget, and when could the window index overflow it?
2. **Quantify the burst.** Prove the worst case admits up to `2 · maxRequests` within a single window's width. Which downstream systems can and can't tolerate that?
3. **Per-key limiting.** Extend to a limit per client key. How do you store per-key counters and evict idle keys without an unbounded map?
4. **Injectable clock.** Refactor the time source so tests are deterministic; verify the reset-on-rollover logic without real sleeps.

## Offline check

Assert that within any fixed window no more than `maxRequests` are granted: bucket the wall-clock of each successful acquire by `t / windowMillis` and assert every bucket's count `<= maxRequests`. A non-atomic `if (count < max) count++` will breach this under contention.

---
---

# 2. Design a Sliding Window Log Rate Limiter

**Difficulty:** Medium · **Topics:** Design, Concurrency, Queue · **Companies:** Cloudflare, Uber

---

Keep a **log of timestamps**, one per admitted request. When a request arrives at `now`, first evict every logged timestamp older than the rolling window (`t <= now - windowMillis`). If the number of surviving timestamps is below the limit, admit the request, append `now` to the log, and return `true`. Otherwise reject without modifying the log.

Unlike the fixed window, the window truly slides with each request, so there is no boundary burst — but the log holds an entry for every admitted request currently inside the window.

Design the `SlidingWindowLog` class:

- `SlidingWindowLog(int maxRequests, long windowMillis)`
- `boolean tryAcquire()` — evicts aged-out timestamps, then admits iff fewer than `maxRequests` remain.

**Thread-safety requirement.** `tryAcquire` is called concurrently from many threads. The evict → count → conditionally-append sequence must be **atomic as a whole**:

> At any instant, the number of logged timestamps within `(now - windowMillis, now]` never exceeds `maxRequests`, and every admitted request appends exactly one timestamp.

## Example 1

`SlidingWindowLog(3, 1000)` — at most 3 requests in any rolling 1000 ms.

| Wall clock | Log after eviction | Survivors | Result | Log after |
|---|---|---|---|---|
| 0 ms | `[]` | 0 | `true` | `[0]` |
| 300 ms | `[0, 300... ]`→`[0]` | 1 | `true` | `[0, 300]` |
| 600 ms | `[0, 300]` | 2 | `true` | `[0, 300, 600]` |
| 700 ms | `[0, 300, 600]` | 3 | `false` | `[0, 300, 600]` |
| 1100 ms | `[300, 600]` (evict 0) | 2 | `true` | `[300, 600, 1100]` |
| 1300 ms | `[600, 1100]` (evict 300) | 2 | `true` | `[600, 1100, 1300]` |

**Explanation.** The first three requests fill the window and the fourth (700 ms) is rejected because all of 0, 300, 600 are still within the last 1000 ms. By 1100 ms the timestamp 0 has aged out (`0 <= 1100 - 1000`), freeing a slot, so the request is admitted. At 1300 ms, 300 ages out. There is no boundary artifact — the constraint is enforced continuously — but the log's size tracks live traffic.

## Constraints

- `1 <= maxRequests <= 10^5`
- `1 <= windowMillis <= 10^9`
- Calls may be made concurrently from up to `64` threads.
- At most `10^6` total calls.

## Follow-ups

1. **Why not lock-free?** The operation reads *and* mutates a whole collection conditionally, so a single CAS won't cover it. Guard it with a `ReentrantLock` (or `synchronized`). Argue why a bare `ConcurrentLinkedDeque` is insufficient on its own even though each of its ops is thread-safe.
2. **Bound the memory to O(maxRequests).** You never need more than `maxRequests` timestamps: replace the unbounded log with a **circular buffer** of size `maxRequests` holding the last admissions. A new request is allowed iff the oldest slot is outside the window. This makes both time and space O(1) per call regardless of traffic. Implement it.
3. **Per-key.** A log per client key, with idle-key eviction. How does the ring-buffer variant change the memory story across millions of keys?
4. **Injectable clock** for deterministic tests.

## Offline check

Same harness. Assert that for every admitted request, the count of admitted timestamps within `(t - windowMillis, t]` is `<= maxRequests`. A race in the evict-count-append critical section will let two threads both observe `maxRequests - 1` survivors and both append.

---
---

# 3. Design a Sliding Window Counter Rate Limiter

**Difficulty:** Medium · **Topics:** Design, Concurrency, Math · **Companies:** Cloudflare, Stripe

---

A hybrid of the fixed window and the sliding log that keeps the sliding behavior with O(1) memory. Track the admitted count for the **current** fixed window and the **previous** one. Approximate the rolling count by weighting the previous window by how much of it still overlaps the rolling window:

```
elapsed  = now - currentWindowStart          // 0 <= elapsed < W
overlap  = (W - elapsed) / W                  // fraction of previous window still in view
estimate = currentCount + previousCount * overlap
```

Admit the request iff `estimate < maxRequests`, incrementing `currentCount`. This assumes the previous window's requests were spread uniformly — an approximation, but a cheap and accurate-enough one.

Design the `SlidingWindowCounter` class:

- `SlidingWindowCounter(int maxRequests, long windowMillis)`
- `boolean tryAcquire()` — rolls the window forward if needed (on rollover, `previous = current`, `current = 0`; if more than one full window has elapsed idle, `previous = 0` too), computes `estimate`, and admits iff `estimate < maxRequests`.

**Thread-safety requirement.** State is `{windowIndex, previousCount, currentCount}`. The roll-forward → estimate → conditional-increment sequence must be atomic:

> No interleaving may admit a request whose computed `estimate` is `>= maxRequests`, and a stale window must never be treated as current.

## Example 1

`SlidingWindowCounter(5, 1000)`. Window 0 = `[0, 1000)`, window 1 = `[1000, 2000)`.

| Wall clock | Window (elapsed) | overlap | estimate | Result | current after |
|---|---|---|---|---|---|
| 200 ms | 0 (200) | — | `0 + 0 = 0.0` | `true` | 1 |
| 400 ms | 0 (400) | — | `1 + 0 = 1.0` | `true` | 2 |
| 600 ms | 0 (600) | — | `2 + 0 = 2.0` | `true` | 3 |
| 800 ms | 0 (800) | — | `3 + 0 = 3.0` | `true` | 4 |
| 900 ms | 0 (900) | — | `4 + 0 = 4.0` | `true` | 5 |
| 950 ms | 0 (950) | — | `5 + 0 = 5.0` | `false` | 5 |
| 1200 ms | 1 (200) | 0.8 | `0 + 5·0.8 = 4.0` | `true` | 1 |
| 1300 ms | 1 (300) | 0.7 | `1 + 5·0.7 = 4.5` | `true` | 2 |
| 1400 ms | 1 (400) | 0.6 | `2 + 5·0.6 = 5.0` | `false` | 2 |

**Explanation.** Window 0 admits 5 and then rejects. When the clock enters window 1, the previous window's full count of 5 still carries almost all its weight (overlap 0.8 at 1200 ms), so the estimate is 4.0 and only a trickle is admitted. As we move deeper into window 1 the previous window's weight decays, gradually opening capacity. Contrast a fixed window, which would reset to 0 at 1000 ms and immediately admit 5 more — the sliding counter smooths that cliff using just two integers.

## Constraints

- `1 <= maxRequests <= 10^9`
- `1 <= windowMillis <= 10^9`
- Calls may be made concurrently from up to `64` threads.
- At most `10^6` total calls.

## Follow-ups

1. **Lock-free.** Use an `AtomicReference` to an immutable `{windowIndex, previousCount, currentCount}` snapshot in a CAS retry loop. The trap: a caller arriving after a **long idle gap** (more than one window) must zero out `previousCount`, not carry a stale value — get this right inside the CAS.
2. **Approximation error.** Construct traffic where the estimate admits slightly more or fewer requests than a true sliding log would, and explain why (the uniform-distribution assumption).
3. **Determinism.** Should `estimate` use floating point or fixed-point? What makes the weighted comparison reproducible across runs and machines?
4. **Injectable clock.**

## Offline check

The exact invariant is approximate by design, so assert the algorithm's own rule rather than a hard count: log each call's `estimate` and result, and assert every admitted call had `estimate < maxRequests` under the serialized post-hoc reconstruction. Any lost update shows up as two admits computed from the same `currentCount`.

---
---

# 4. Design a Leaky Bucket Rate Limiter

**Difficulty:** Medium · **Topics:** Design, Concurrency, Producer-Consumer, Blocking Queue · **Companies:** AWS, Uber

---

Model requests as water entering a bucket of fixed capacity. The bucket **leaks at a constant rate** — one request drains every `leakIntervalMillis`, regardless of how fast requests arrive. A request that arrives when the bucket is full **overflows and is dropped**. Where the token bucket permits bursts, the leaky bucket enforces a smooth, constant *output* rate: bursty input, evenly-spaced output.

This is a producer/consumer system: many callers offer requests (producers); a single leaker drains at a fixed cadence (consumer).

Design the `LeakyBucket` class:

- `LeakyBucket(int capacity, long leakIntervalMillis)`
  A bucket holding at most `capacity` queued requests, draining one every `leakIntervalMillis`.

- `boolean offer()`
  Non-blocking. If the bucket has room, enqueues the request (FIFO) and returns `true`. If the bucket is full, drops the request and returns `false`.

- `int size()`
  Current number of queued requests (for observation/testing).

Internally a background leaker removes the head of the queue once every `leakIntervalMillis` when the bucket is non-empty; while empty, nothing leaks.

**Thread-safety requirement.** Many threads call `offer()` concurrently while the leaker drains. Invariants for every interleaving:

> `size()` never exceeds `capacity`; no two concurrent `offer()` calls ever occupy the same slot (the bucket never overflows its capacity); FIFO order is preserved; and successive leaks are spaced at least `leakIntervalMillis` apart, so the sustained output rate never exceeds `1 / leakIntervalMillis`.

## Example 1

`LeakyBucket(3, 500)` — capacity 3, leaks 1 every 500 ms. Five requests arrive together at t=0.

| Wall clock | Event | Result | size after |
|---|---|---|---|
| 0 ms | `offer()` | `true` | 1 |
| 0 ms | `offer()` | `true` | 2 |
| 0 ms | `offer()` | `true` | 3 |
| 0 ms | `offer()` | `false` (full) | 3 |
| 0 ms | `offer()` | `false` (full) | 3 |
| 500 ms | leak | — | 2 |
| 600 ms | `offer()` | `true` | 3 |
| 1000 ms | leak | — | 2 |
| 1500 ms | leak | — | 1 |
| 2000 ms | leak | — | 0 |

**Explanation.** The bucket accepts the burst only up to its capacity of 3; the 4th and 5th simultaneous requests overflow and are dropped. The queue then drains at a strict one-per-500 ms, so the **output** is a smooth 2 requests/second no matter how bursty the input was — the defining property of the leaky bucket, and the contrast with the token bucket (which would have released the whole burst at once given enough tokens).

## Constraints

- `1 <= capacity <= 10^6`
- `1 <= leakIntervalMillis <= 10^9`
- `offer()` may be called concurrently from up to `64` producer threads.
- At most `10^6` total calls.

## Follow-ups

1. **Two implementations.** First with `ArrayBlockingQueue(capacity)` for `offer()` plus a `ScheduledExecutorService` calling `poll()` every `leakIntervalMillis`. Then **hand-roll** the bounded buffer with a `ReentrantLock` and two `Condition`s (`notFull`, `notEmpty`) and a leaker that waits the interval — an extension of the classic Bounded Blocking Queue with a timed consumer.
2. **Blocking / shaping variant.** Add `void submit()` that blocks the caller until its request leaks, instead of dropping on overflow. Producers now experience back-pressure. Should the wait be bounded, and how do you avoid lost signals between `notFull`/`notEmpty`?
3. **Leak timing.** Compare `scheduleAtFixedRate` against computing the next-leak instant yourself. What happens to spacing if a leak's handling is delayed — do you catch up in a burst or skip? Which preserves the output-rate invariant?
4. **Lazy meter (no thread).** Replace the real queue and leaker with a single **water-level** counter that leaks lazily on each `offer()`: `level = max(0, level - elapsed / leakInterval)`; admit iff `level + 1 <= capacity`. Show this is the mathematical dual of the token bucket, and say when the lazy meter is preferable to a real queue (no idle thread) and when it isn't (no true FIFO shaping of real payloads).
5. **Injectable clock** for deterministic leak-timing tests.

## Offline check

Two assertions. Safety: sample `size()` across the run and assert it never exceeds `capacity`; also assert total dropped + total leaked + current size equals total offered. Rate: record leak timestamps and assert consecutive gaps are `>= leakIntervalMillis`, so the measured output rate stays at or below `1 / leakIntervalMillis`.
