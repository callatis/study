package org.callatis.study.solutions;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LRUCacheLCTest {

    @Test
    public void testExample1() {
        // Operations:
        // ["LRUCache","put","put","get","put","get","put","get","get","get"]
        // [[2],[1,1],[2,2],[1],[3,3],[2],[4,4],[1],[3],[4]]
        // Output: [null,null,null,1,null,-1,null,-1,3,4]
        LRUCacheLC cache = new LRUCacheLC(2);

        cache.put(1, 1); // cache is {1=1}
        cache.put(2, 2); // cache is {1=1, 2=2}
        assertEquals(1, cache.get(1)); // return 1
        cache.put(3, 3); // evicts key 2, cache is {1=1, 3=3}
        assertEquals(-1, cache.get(2)); // returns -1 (not found)
        cache.put(4, 4); // evicts key 1, cache is {4=4, 3=3}
        assertEquals(-1, cache.get(1)); // return -1 (not found)
        assertEquals(3, cache.get(3)); // return 3
        assertEquals(4, cache.get(4)); // return 4
    }

    @Test
    public void testExample2() {
        // ["LRUCache","put","get","put","get","get"]
        // [[1],[2,1],[2],[3,2],[2],[3]]  (capacity 1)
        // Expected returns: [null, null, 1, null, -1, 2]
        LRUCacheLC cache = new LRUCacheLC(1);

        cache.put(2, 1); // {2=1}
        assertEquals(1, cache.get(2)); // return 1
        cache.put(3, 2); // LRU key was 2, evicts key 2 -> {3=2}
        assertEquals(-1, cache.get(2)); // key 2 was evicted, return -1
        assertEquals(2, cache.get(3)); // return 2
    }

    @Test
    public void testExample3() {
        // Operations (doc labels are swapped; expected returns derived from LRU semantics):
        // ["LRUCache","put","put","put","put","get","get","get","get","put","get","get","get","get","get"]
        // [[3],[1,1],[2,2],[3,3],[4,4],[4],[3],[2],[1],[5,5],[1],[2],[3],[4],[5]]
        LRUCacheLC cache = new LRUCacheLC(3);

        cache.put(1, 1); // {1=1}
        cache.put(2, 2); // {1=1, 2=2}
        cache.put(3, 3); // {1=1, 2=2, 3=3}
        cache.put(4, 4); // evicts LRU key 1 -> {2=2, 3=3, 4=4}
        assertEquals(4, cache.get(4)); // return 4
        assertEquals(3, cache.get(3)); // return 3
        assertEquals(2, cache.get(2)); // return 2
        assertEquals(-1, cache.get(1)); // key 1 was evicted, return -1
        cache.put(5, 5); // LRU is key 4, evicts key 4 -> {2=2, 3=3, 5=5}
        assertEquals(-1, cache.get(1)); // still absent, return -1
        assertEquals(2, cache.get(2)); // return 2
        assertEquals(3, cache.get(3)); // return 3
        assertEquals(-1, cache.get(4)); // key 4 was evicted, return -1
        assertEquals(5, cache.get(5)); // return 5
    }

    @Test
    public void testGetMissingKeyReturnsMinusOne() {
        LRUCacheLC cache = new LRUCacheLC(1);
        assertEquals(-1, cache.get(42));
    }

    @Test
    public void testPutUpdatesExistingValue() {
        LRUCacheLC cache = new LRUCacheLC(2);
        cache.put(1, 1);
        cache.put(1, 10);
        assertEquals(10, cache.get(1));
    }

    @Test
    public void testGetRefreshesRecencyToAvoidEviction() {
        LRUCacheLC cache = new LRUCacheLC(2);
        cache.put(1, 1);
        cache.put(2, 2);
        assertEquals(1, cache.get(1)); // 1 becomes most recently used
        cache.put(3, 3); // should evict 2 (least recently used), not 1
        assertEquals(-1, cache.get(2));
        assertEquals(1, cache.get(1));
        assertEquals(3, cache.get(3));
    }

    @Test
    public void testUpdatingValueRefreshesRecency() {
        LRUCacheLC cache = new LRUCacheLC(2);
        cache.put(1, 1);
        cache.put(2, 2);
        cache.put(1, 10); // updates key 1 and makes it most recently used
        cache.put(3, 3); // should evict 2, not 1
        assertEquals(-1, cache.get(2));
        assertEquals(10, cache.get(1));
        assertEquals(3, cache.get(3));
    }

    @Test
    public void testCapacityOne() {
        LRUCacheLC cache = new LRUCacheLC(1);
        cache.put(1, 1);
        assertEquals(1, cache.get(1));
        cache.put(2, 2); // evicts key 1
        assertEquals(-1, cache.get(1));
        assertEquals(2, cache.get(2));
    }
}
