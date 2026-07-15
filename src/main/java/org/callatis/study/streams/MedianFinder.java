package org.callatis.study.streams;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MedianFinder {

    private final PriorityQueue<Integer> pqL, pqR;

    private final int size;

    private final int[] nums;


    public MedianFinder(int size) {
        this.size = size;
        if (size > 0) {
            this.pqL = null;
            this.pqR = null;
            this.nums = new int[size];
        } else {
            this.nums = null;
            // Max-heap: highest element at the top
            this.pqL = new PriorityQueue<>(Comparator.reverseOrder());
            // Min-heap: lowest element at the top (default, natural ordering)
            this.pqR = new PriorityQueue<>();
        }
    }
    
    public void addNum(int num) {
        if (this.size > 0) {
            addNumSmall(num);
        } else {
            addNumLarge(num);
        }
    }

    public double findMedian() {
        if (this.size > 0) {
            return findMedianSmall();
        }
        return findMedianLarge();
    }

    private void addNumSmall(int num) {
        this.nums[num]++;
    }

    private int findKth(int k) {
        int current = 0, i = 0;
        while (current < k && i < this.size) {
            if (this.nums[i] > 0) {
                current += this.nums[i];
            } 
            i++;
        }

        return i == this.size ? -1 : i - 1;
    }

    private double findMedianSmall() {
        int count = 0;
        for (int i = 0; i < this.size; i++) {
            count += this.nums[i];
        }

        int k;
        // if count is even, then count == 2k => median is avg((k-1)th, kth)
        if (count % 2 == 0) {
            k = count / 2;
            int left = findKth(k);
            double right = findKth(k + 1);
            return ((double) left + right) / 2;
        }
        k = count / 2;
        return findKth(k + 1);
    }

    private void addNumLarge(int num) {
        if (this.pqL.isEmpty() || num < this.pqL.peek()) { // goes to the left
            this.pqL.add(num);
        } else { // goes right
            this.pqR.add(num);
        }
        // rebalance
        if (this.pqL.size() + 1 < this.pqR.size()) { // 2+ too right
            this.pqL.add(this.pqR.poll());
        } else if (this.pqL.size() > this.pqR.size() + 1) { // 2+ too left
            this.pqR.add(this.pqL.poll());
        }
    }
    
    private double findMedianLarge() {
        if (this.pqL.size() < this.pqR.size()) {
            return this.pqR.peek();
        } else if (this.pqL.size() > this.pqR.size()) {
            return this.pqL.peek();
        }
        return (((double) this.pqR.peek()) + ((double) this.pqL.peek())) / 2;
    }

}

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder obj = new MedianFinder();
 * obj.addNum(num);
 * double param_2 = obj.findMedian();
 */