package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ThreeSumClosest {

    private final String implementation;

    /* package */ ThreeSumClosest() {
        this("optimized");
    }

    /* package */ ThreeSumClosest(String implementation) {
        this.implementation = implementation;
    }

    public int threeSumClosest(int[] nums, int target) {
        if ("bruteForce".equals(implementation)) {
            return threeSumClosestBruteForce(nums, target);
        }
        if ("partialOptimized".equals(implementation)) {
            return threeSumClosestPartialOptimiz(nums, target);
        }
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

    private NavigableMap<Integer, List<IntPair>> buildTwoSumMap(int[] nums) {
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
        return intPairMap;
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

    public int threeSumClosestPartialOptimiz(int[] nums, int target) {
        NavigableMap<Integer, List<IntPair>> twoSumMap = buildTwoSumMap(nums); // n^2
        Integer maxSum  = null;
        // iterate over each index - O(n)
        for (int k = 0; k < nums.length; k++) { // n * 
            int deltaValue = target - nums[k];
            Integer mapValue = getClosestValue(twoSumMap, k, deltaValue); // log(n)
            if (maxSum == null || (mapValue != null && Math.abs(target - nums[k] - mapValue) < Math.abs(target - maxSum))) {
                maxSum = nums[k] + mapValue;
            }
        }

        return maxSum;
    }

    private Integer getClosestValue(NavigableMap<Integer, List<IntPair>> twoSumMap, int k, int deltaValue) {
        // identify the closest 2 values to the current complement to target;
        // i.e. the 3-sum is the closest to target
        // Note: if k is either i or j, we need to move left, resp. right. 
        Integer lower = twoSumMap.floorKey(deltaValue);
        while (lower != null && intPairList(twoSumMap.get(lower), k)) {
            lower = twoSumMap.floorKey(lower - 1);
        }
        Integer upper = twoSumMap.ceilingKey(deltaValue);
        while (upper != null && (intPairList(twoSumMap.get(upper), k))) { 
            upper = twoSumMap.ceilingKey(upper + 1);
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
        return mapValue;
    }

    public int threeSumClosestOptimized(int[] nums, int target) {
        Arrays.sort(nums);
        int maxSum = nums[0] + nums[1] + nums[2];
        int minDiff = maxSum < target ? target - maxSum : maxSum - target;
        int numsLen = nums.length;
        // for (int j = 0; j < nums.length; j++) {
        //     System.out.println("nums[" + j + "] = " + nums[j]);
        // }
        // System.out.println("================");
        for (int i = 0; i < numsLen - 2; i++) {
            // System.out.println("nums[" + i + "] = " + nums[i]);
            // System.out.println(i + ": " + target + " - " + nums[i] + " = " + (target - nums[i]));
            // System.out.println("----------------");
            int j = i + 1, k = numsLen - 1;
            while (j < k) {
                // System.out.println("nums[" + j + "] = " + nums[j]);
                // System.out.println("nums[" + k + "] = " + nums[k]);
                int currSum = nums[i] + nums[j] + nums[k];
                int currDiff = Math.abs(target - currSum);
                if (currDiff == 0) { // Bingo!
                    return currSum;
                }
                // System.out.println(j + ", " + k + ": " + twoValue);
                if (minDiff > currDiff) {
                    // System.out.print(maxSum + " --> ");
                    maxSum = currSum;
                    // System.out.println(maxSum);
                    minDiff = currDiff;
                }
                if (currSum < target) {
                    j++;
                } else {
                    k--;
                }
            }
            // System.out.println("======== " + i + " ========");
        }

        return maxSum;
    }

}