package org.callatis.study.streams;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MedianFinder {

    private final PriorityQueue<Integer> pqL, pqR;

    public MedianFinder() {
        // Max-heap: highest element at the top
        this.pqL = new PriorityQueue<>(Comparator.reverseOrder());
        // Min-heap: lowest element at the top (default, natural ordering)
        this.pqR = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        if (this.pqL.isEmpty() || num < this.pqL.peek()) { // goes to the left
            this.pqL.add(num);
        } else { // goes right
            this.pqR.add(num);
        }
        // rebalance
        if (this.pqL.size() + 1 < this.pqR.size()) { // 2+ too right
            this.pqL.add(this.pqR.remove());
        } else if (this.pqL.size() > this.pqR.size() + 1) { // 2+ too left
            this.pqR.add(this.pqL.remove());
        }
    }
    
    public double findMedian() {
        if (this.pqL.size() < this.pqR.size()) {
            return this.pqR.peek();
        } else if (this.pqL.size() > this.pqR.size()) {
            return this.pqL.peek();
        }
        return ((double) (this.pqR.peek() + this.pqL.peek())) / 2;
    }

}

/**
 * Your MedianFinder object will be instantiated and called as such:
 * MedianFinder obj = new MedianFinder();
 * obj.addNum(num);
 * double param_2 = obj.findMedian();
 */