package org.callatis.study.streams;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class SlidingWindowMedian {

    
    private int getTopSkipRemoved(PriorityQueue<Integer> pq, Map<Integer, Integer> removedMap, boolean remove) {
        int top = remove ? pq.poll() : pq.peek();
        Integer count = removedMap.get(top);
        while (count != null && count > 0) {
            removedMap.put(top, count - 1);
            if (remove) {
                top = pq.poll();
            } else {
                pq.poll(); // we already have this element
                top = pq.peek();
            }
            count = removedMap.get(top);
        }
        return top;
    }

    private void rebalance(PriorityQueue<Integer> pqL, PriorityQueue<Integer> pqR, Map<Integer, Integer> removedMap) {
        if (pqR.size() + 1 < pqL.size()) {
            Integer polled = getTopSkipRemoved(pqL, removedMap, true);
            pqR.add(polled);
        } else if (pqR.size() > pqL.size() + 1) {
            Integer polled = getTopSkipRemoved(pqR, removedMap, true);
            pqL.add(polled);
        }    
    }    

    private double calcMedian(PriorityQueue<Integer> pqL, PriorityQueue<Integer> pqR, Map<Integer, Integer> removedMap) {
        if ((pqR.size() + pqL.size()) % 2 == 1) { // odd - return the middle
            if (pqR.size() > pqL.size()) {
                return getTopSkipRemoved(pqR, removedMap, false);
            }
            return getTopSkipRemoved(pqL, removedMap, false);
        }

        return (((double) pqL.peek()) + ((double) pqR.peek())) / 2;
    }

    public double[] medianSlidingWindow(int[] nums, int k) {
        double[] doubles = new double[nums.length - k + 1];
        PriorityQueue<Integer> pqR = new PriorityQueue<>();
        PriorityQueue<Integer> pqL = new PriorityQueue<>(Comparator.reverseOrder());
        Map<Integer, Integer> removedMap = new HashMap<>();
        // build the two PQs for the first sliding window
        for (int i = 0; i < k; i++) {
            addToHeaps(nums[i], pqL, pqR);
            rebalance(pqL, pqR, removedMap);
        }
        int j = 0;
        doubles[j++] = calcMedian(pqL, pqR, removedMap);
        for (int i = 1; i < nums.length - k + 1; i++) {
            removeFromHeaps(nums[i - 1], removedMap);
            addToHeaps(nums[i + k - 1], pqL, pqR);
            rebalance(pqL, pqR, removedMap);
            doubles[j++] = calcMedian(pqL, pqR, removedMap);
        }

        return doubles;
    }

    private void addToHeaps(int num, PriorityQueue<Integer> pqL, PriorityQueue<Integer> pqR) {
        if (!pqL.isEmpty() && num <= pqL.peek()) {
            pqL.add(num);
        } else if (!pqR.isEmpty() && num > pqR.peek()) {
            pqR.add(num);
        } else { // at least one queue is empty, and it doesn't necessarily belong in the non-empty one
            pqL.add(num);
        }
    }

    private void removeFromHeaps(int num, Map<Integer, Integer> removedMap) {
        Integer removeCount = removedMap.get(num);
        if (removeCount == null) {
            removeCount = 0;
        }
        removedMap.put(num, removeCount + 1);
        // boolean removed = false;
        // if (!pqL.isEmpty() && num <= pqL.peek()) {
        //     removed = pqL.remove(num);
        // }
        // if (!removed) {
        //     removed = pqR.remove(num);
        // }
        // if (!removed) {
        //     throw new IllegalStateException(num + " was not found in either heap");
        // }
    }


}
