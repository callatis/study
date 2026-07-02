# Producer-Consumer Problem

The Producer-Consumer problem is a classic synchronization scenario. 

## Problem Statement

    There is a shared buffer (queue) with a fixed capacity
    Producers generate data items and add them to the buffer
    Consumers remove data items from the buffer and process them
    The buffer is a shared resource that must be accessed safely by multiple threads

## Constraints

    If the buffer is full, producers must wait until space is available
    If the buffer is empty, consumers must wait until items are available
    Only one thread can access the buffer at a time (mutual exclusion)
    No items should be lost or duplicated

## Requirements

    Implement thread-safe add and remove operations
    Handle blocking when buffer is full/empty
    Prevent race conditions and deadlocks

## Common Interview Variations

    Implement using wait() and notify()
    Implement using ReentrantLock and Condition
    Implement using BlockingQueue
    Multiple producers and multiple consumers
    Priority-based consumption

## Examples
Example 1: 
    1 Producer, producing 10 items with no delay
    1 Consumer, consuming 10 items with no delay

Example 2: 
    1 Producer, producing 10 items with 1000ms delay
    1 Consumer, consuming 10 items with 1000ms delay

Example 3: 
    2 Producers, producing 10 items each, with 1000ms delay
    1 Consumer, consuming 20 items with no delay

Example 4:
    1 Producer, producing 20 items with no delay
    2 Consumers, each consuming 10 items with 1000ms delay

Example 5:
    4 Producers, each producing 5 items with 1000ms delay
    2 Consumers, each consuming 10 items with 500ms delay

Example 6:
    3 Producers, each producing 20 items with no delay
    6 Consumers, each consuming 10 items with no delay

Example 7:
    6 Producers, each producing 10 items with no delay
    3 Consumers, each consuming 20 items with 2000ms delay

Example 8:
    3 Producers, each producing 20 items with 2000ms delay
    6 Consumers, each consuming 10 items with no delay

Example 9:
    8 Producers, each producing 25 items with 100ms delay
    5 Consumers, each consuming 40 items with 100ms delay

Example 10:
    10 Producers, each producing 10 items with no delay
    10 Consumers, each consuming 10 items with no delay

Example 11:
    2 Producers, each producing 100 items with no delay
    1 Consumer, consuming 200 items with 50ms delay

Example 12:
    1 Producer, producing 200 items with 50ms delay
    4 Consumers, each consuming 50 items with no delay

Example 13:
    5 Producers, each producing 40 items with 250ms delay
    4 Consumers, each consuming 50 items with 750ms delay

Example 14:
    12 Producers, each producing 5 items with no delay
    3 Consumers, each consuming 20 items with 1500ms delay

Example 15:
    3 Producers, each producing 20 items with 1500ms delay
    12 Consumers, each consuming 5 items with no delay

Example 16:
    16 Producers, each producing 50 items with 10ms delay
    20 Consumers, each consuming 40 items with 10ms delay

Notes:
    In each example, total produced items equals total consumed items.
    These combinations cover balanced throughput, producer-heavy contention (queue often full),
    and consumer-heavy contention (queue often empty).
