# LeetCode Concurrency Problems with Difficulty:
| Problem ID | Title | Difficulty |
| --- | --- | --- |
| 1114 | Print in Order | Easy |
| 1115 | Print FooBar Alternately | Medium |
| 1116 | Print Zero Even Odd | Medium |
| 1117 | Building H2O | Medium |
| 1188 | Design Bounded Blocking Queue | Medium |
| 1195 | Fizz Buzz Multithreaded | Medium |
| 1242 | Web Crawler Multithreaded | Medium |
| 1279 | Traffic Light Controlled Intersection | Medium |

# Producer-Consumer Problem:

The Producer-Consumer problem is a classic synchronization scenario:

Problem Statement:

    There is a shared buffer (queue) with a fixed capacity
    Producers generate data items and add them to the buffer
    Consumers remove data items from the buffer and process them
    The buffer is a shared resource that must be accessed safely by multiple threads

Constraints:

    If the buffer is full, producers must wait until space is available
    If the buffer is empty, consumers must wait until items are available
    Only one thread can access the buffer at a time (mutual exclusion)
    No items should be lost or duplicated

Requirements:

    Implement thread-safe add and remove operations
    Handle blocking when buffer is full/empty
    Prevent race conditions and deadlocks

Common Interview Variations:

    Implement using wait() and notify()
    Implement using ReentrantLock and Condition
    Implement using BlockingQueue
    Multiple producers and multiple consumers
    Priority-based consumption
