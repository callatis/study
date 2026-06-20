package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ThreeSumClosest {

    public int threeSumClosest(int[] nums, int target) {
        return threeSumClosestOptimized(nums, target);
    }

    public int threeSumClosestBruteForce(int[] nums, int target) {
        int minSum = target-Integer.MAX_VALUE;
        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    // System.out.println("Evaluating "+ i + ", " + j + ", " + k);
                    int newSum = nums[i] + nums[j] + nums[k];
                    if (Math.abs(target - minSum) > Math.abs(target - newSum)) {
                        minSum = newSum;
                        // System.out.println("Found " + i + ", " + j + ", " + k);
                    }
                }
            }
        }

        return minSum;
    }

    private class IntPair {
        private int i;
        private int j;
        private IntPair(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    private boolean intPairList(List<IntPair> intPairList, int k) {
        for (IntPair intPair: intPairList) {
            if (intPair.i != k && intPair.j != k) {
                return false;
            }
        }
        return true;
    }

    public int threeSumClosestOptimized(int[] nums, int target) {
        // build a navigable map of the sum of any 2 different indices to their pair
        // should not have the diagonal [i, i], since it's not an allowed combination
        // O(n^2 * log(n))
        NavigableMap<Integer, List<IntPair>> intPairMap = new TreeMap<>();
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int value = nums[i] + nums[j];
                IntPair intPair = new IntPair(i, j);
                List<IntPair> list = intPairMap.get(value);
                if (list == null) { // first time we're adding a value for this key
                    list = new ArrayList<>();
                    intPairMap.put(value, list);
                }
                list.add(intPair);
            }
        }
        Integer maxSum  = null;
        // iterate over each index - O(n)
        for (int k = 0; k < nums.length; k++) {
            int deltaValue = target - nums[k];
            // identify the closest 2 values to the current complement to target;
            // i.e. the 3-sum is the closest to target
            // Note: if k is either i or j, we need to move left, resp. right. 
            Integer lower = intPairMap.floorKey(deltaValue);
            while (lower != null && intPairList(intPairMap.get(lower), k)) {
                lower = intPairMap.floorKey(lower - 1);
            }
            Integer upper = intPairMap.ceilingKey(deltaValue);
            while (upper != null && (intPairList(intPairMap.get(upper), k))) { 
                upper = intPairMap.ceilingKey(upper + 1);
            };
            // identify which one is the closest
            Integer mapValue;
            if (lower == null) {
                 mapValue = upper;
            } else if (upper == null) {
                mapValue = lower;
            } else if (Math.abs(deltaValue - lower) < Math.abs(upper - deltaValue)) {
                // closer to lower
                mapValue = lower;
            } else {
                // closer to upper
                mapValue = upper;
            }
            if (maxSum == null || (mapValue != null && Math.abs(target - nums[k] - mapValue) < Math.abs(target - maxSum))) {
                maxSum = nums[k] + mapValue;
            }
        }
        // I have a map index -> delta from target
        // I have the sum of any 2 other indices
        // the question now is: which one of the 2 other indices gets the closest to the delta
        // so if the sums of 2 [other] indices is in an ordered binary tree, then I could search for each delta and find the closest to it, and store this.

        return maxSum;
    }

}
