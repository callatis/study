package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.callatis.study.utils.Pair;

/**
 * Given an integer array and a {@code target}, finds the sum of the three elements whose total
 * is closest to {@code target} (LeetCode 16). Every input is assumed to have exactly one
 * closest sum.
 *
 * <p>Three strategies are provided, selectable through the constructor:</p>
 * <ul>
 *   <li>{@code "bruteForce"} &mdash; check every triplet in {@code O(n^3)};</li>
 *   <li>{@code "partialOptimized"} &mdash; precompute all pairwise sums in a {@link TreeMap} and,
 *       for each element, look up the nearest complement in {@code O(n^2 log n)};</li>
 *   <li>{@code "optimized"} (default) &mdash; sort then run a two-pointer scan in {@code O(n^2)}.</li>
 * </ul>
 */
public class ThreeSumClosest {

    private final String implementation;

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

    /**
     * Baseline {@code O(n^3)} solution: examine every distinct triplet and keep the sum with the
     * smallest absolute distance to {@code target}.
     *
     * @param nums   the input array
     * @param target the value the triplet sum should approach
     * @return the closest achievable triplet sum
     */
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

    /**
     * Maps every pairwise sum {@code nums[i] + nums[j]} (with {@code i < j}) to the list of index
     * pairs that produce it, keyed in a {@link NavigableMap} so complements can be looked up by
     * proximity. Runs in {@code O(n^2 log n)}.
     *
     * @param nums the input array
     * @return a navigable map from pair-sum to the index pairs producing it
     */
    private NavigableMap<Integer, List<Pair<Integer>>> buildTwoSumMap(int[] nums) {
        // build a navigable map of the sum of any 2 different indices to their pair
        // should not have the diagonal [i, i], since it's not an allowed combination
        // O(n^2 * log(n))
        NavigableMap<Integer, List<Pair<Integer>>> intPairMap = new TreeMap<>();
        for (int i = 0; i < nums.length - 1; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int value = nums[i] + nums[j];
                Pair<Integer> intPair = new Pair<>();
                intPair.x = i;
                intPair.y = j;
                List<Pair<Integer>> list = intPairMap.get(value);
                if (list == null) { // first time we're adding a value for this key
                    list = new ArrayList<>();
                    intPairMap.put(value, list);
                }
                list.add(intPair);
            }
        }
        return intPairMap;
    }

    /**
     * Reports whether <em>every</em> index pair that produced a given two-sum reuses index
     * {@code k}. Such a bucket is unusable as a complement for element {@code k} (an index cannot
     * be part of its own triplet), so the caller must skip to the next-nearest key.
     *
     * @param intPairList the index pairs stored under one two-sum key
     * @param k           the index currently being completed into a triplet
     * @return {@code true} if no pair in the bucket avoids {@code k}
     */
    private boolean intPairList(List<Pair<Integer>> intPairList, int k) {
        for (Pair<Integer> intPair: intPairList) {
            if (intPair.x != k && intPair.y != k) {
                return false;
            }
        }
        return true;
    }

    /**
     * Precomputes all pairwise sums, then for each element {@code nums[k]} looks up the pair sum
     * closest to {@code target - nums[k]}. The map lookups are logarithmic, giving an overall
     * {@code O(n^2 log n)} cost dominated by building the map.
     *
     * @param nums   the input array
     * @param target the value the triplet sum should approach
     * @return the closest achievable triplet sum
     */
    public int threeSumClosestPartialOptimiz(int[] nums, int target) {
        NavigableMap<Integer, List<Pair<Integer>>> twoSumMap = buildTwoSumMap(nums); // n^2
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

    /**
     * Finds the pair sum nearest to {@code deltaValue} while skipping buckets whose only index
     * pairs reuse {@code k}. It probes the {@link NavigableMap#floorKey floor} and
     * {@link NavigableMap#ceilingKey ceiling} of {@code deltaValue}, advancing past any
     * {@code k}-conflicting bucket, then returns whichever surviving key is closer.
     *
     * @param twoSumMap  the precomputed pair-sum index
     * @param k          the index being completed into a triplet
     * @param deltaValue the complement {@code target - nums[k]} we want to approach
     * @return the closest usable pair sum, or {@code null} if none exists
     */
    private Integer getClosestValue(NavigableMap<Integer, List<Pair<Integer>>> twoSumMap, int k, int deltaValue) {
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
        }
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

    /**
     * Sort-plus-two-pointer solution running in {@code O(n^2)}. After sorting, the outer loop
     * fixes {@code nums[i]} and two pointers converge from the ends of the suffix. Because the
     * array is sorted, the running sum is monotonic in the pointers: when the current sum is
     * below {@code target} the only way to grow it is to advance {@code j}, and when it is above
     * {@code target} the only way to shrink it is to retract {@code k}. The closest sum seen so
     * far is tracked continuously, and an exact match ({@code currDiff == 0}) returns early.
     *
     * @param nums   the input array (reordered in place by sorting)
     * @param target the value the triplet sum should approach
     * @return the closest achievable triplet sum
     */
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