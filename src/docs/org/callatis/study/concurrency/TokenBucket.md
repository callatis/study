# Design a Token Bucket Rate Limiter

**Difficulty:** Medium · **Topics:** Design, Concurrency, Atomic Operations · **Companies:** Stripe, AWS, Uber, Cloudflare

---

A **token bucket** admits requests at a controlled long-term rate while still allowing short bursts. Picture a bucket that holds up to a fixed number of tokens. Tokens are added back at a constant rate, up to the bucket's capacity, which acts as a hard ceiling. Each incoming request must remove one or more tokens to proceed; if the bucket doesn't hold enough, the request is refused and **no** tokens are consumed.

Crucially, there is **no background thread** topping up the bucket. Refill is computed *lazily*: whenever a request arrives, you calculate how many tokens should have accrued since the last time the bucket was touched, then decide.

Design the `TokenBucket` class:

- `TokenBucket(long capacity, long refillTokens, long refillPeriodMillis)` Initializes the bucket with the given `capacity`. The bucket **starts full**. Tokens accrue continuously at a rate of `refillTokens` per `refillPeriodMillis` milliseconds — a request arriving partway through a period sees a proportional fraction — and the token count never exceeds `capacity`.  
    
- `boolean tryAcquire()` Equivalent to `tryAcquire(1)`.  
    
- `boolean tryAcquire(int permits)` Non-blocking. First accrues any tokens earned since the last operation. If at least `permits` tokens are then available, consumes exactly `permits` and returns `true`. Otherwise consumes nothing and returns `false`.

**Thread-safety requirement (this is the point of the problem).** `tryAcquire` will be called **concurrently from many threads**. Your implementation must be correct under that concurrency. Specifically, the following invariant must hold for *every* possible interleaving:

Over any interval, the total number of permits granted must never exceed the tokens present at the start of the interval plus the tokens accrued during it.

In other words, two threads hitting a nearly-empty bucket at the same instant must not both succeed off the same tokens (no lost updates), and the bucket must never be driven below zero or above `capacity`.

---

## Example 1

Real time elapses between calls, so instead of LeetCode's flat input/output arrays this trace adds a **wall-clock** column (time since construction). Bucket is `TokenBucket(5, 1, 1000)` — capacity 5, refilling 1 token per second.

| Wall clock | Call | Tokens before | Result | Tokens after |
| :---- | :---- | :---- | :---- | :---- |
| 0 ms | `tryAcquire()` | 5.0 | `true` | 4.0 |
| 0 ms | `tryAcquire()` | 4.0 | `true` | 3.0 |
| 0 ms | `tryAcquire(3)` | 3.0 | `true` | 0.0 |
| 0 ms | `tryAcquire()` | 0.0 | `false` | 0.0 |
| 2000 ms | `tryAcquire(2)` | 2.0 | `true` | 0.0 |
| 2500 ms | `tryAcquire()` | 0.5 | `false` | 0.5 |
| 3000 ms | `tryAcquire()` | 1.0 | `true` | 0.0 |

**Explanation.** The bucket starts full at 5\. The first three calls drain it — the burst of 5 tokens is allowed all at once, which is the behavior that distinguishes token bucket from leaky bucket. The fourth call finds an empty bucket and is refused without consuming anything. After 2 seconds of silence, exactly 2 tokens have accrued, so `tryAcquire(2)` succeeds and empties the bucket again. Half a second later only 0.5 of a token has accrued — less than the 1 requested — so the request is refused (note the fractional accrual survives the failed call). By 3000 ms a full token has accrued and the final call succeeds.

---

## Constraints

- `1 <= capacity <= 10^9`  
- `1 <= refillTokens <= 10^9`  
- `1 <= refillPeriodMillis <= 10^9`  
- `1 <= permits <= capacity`  
- Calls to `tryAcquire` may be made concurrently from up to `64` threads.  
- At most `10^6` total calls to `tryAcquire` across all threads.

---

## Follow-ups

1. **Make it lock-free.** Implement `tryAcquire` without `synchronized` or `ReentrantLock`, using a single `AtomicReference` to an **immutable state snapshot** (`{tokens, lastRefillNanos}`) updated in a `compareAndSet` retry loop. Why must the snapshot be immutable? What does a thread do when its CAS fails? Compare the throughput of this version against a lock-based one under high contention, and explain the difference.  
     
2. **Add a blocking acquire.** Implement `void acquire(int permits)` that waits until enough tokens are available instead of returning `false`. How do you do this *without* busy-spinning on CAS? (Hint: you can compute exactly how long the caller must wait for `permits` tokens to accrue, then park for that duration — but the world may change while you sleep.)  
     
3. **Fairness / starvation.** Under sustained contention, can a thread requesting a large number of permits be starved indefinitely by a stream of single-permit requests? Does a CAS loop provide any ordering guarantee? How would you add fairness, and what does it cost?  
     
4. **Clock choice.** Why should the refill math use `System.nanoTime()` rather than `System.currentTimeMillis()`? Refactor so the time source is injectable, and explain how that lets you write deterministic unit tests for a component whose behavior depends on real elapsed time.  
     
5. **Token representation.** Continuous accrual implies fractional tokens. What breaks if you store the count as an `int` and truncate? Contrast a `double` count against a fixed-point representation (e.g. tracking time-as-tokens in nanoseconds), especially for a very slow refill rate where per-call truncation could starve the bucket forever.

---

## How to test it offline

The judge can't verify the concurrency invariant, so you verify it yourself. Sketch of a stress harness:

- Construct a bucket with a known `capacity` and `refillRate`.  
- Launch N threads (e.g. 64\) that each loop calling `tryAcquire(1)` for a fixed wall-clock duration, each thread counting its own successes.  
- Sum the successes across all threads.  
- Assert: `totalGranted <= capacity + ceil(elapsedNanos * refillRate)`. If the sum ever exceeds that bound, you have a lost-update race.  
- Run it many times (races are probabilistic). A correct lock-free version holds the bound every run; a naive `check-then-decrement` without atomicity will eventually breach it.

