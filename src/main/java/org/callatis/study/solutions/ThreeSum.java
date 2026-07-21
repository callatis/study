package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Finds all unique triplets {@code [a, b, c]} in an array such that {@code a + b + c == 0}.
 *
 * <p>Both implementations share the same core idea: sort the array, then for each index
 * {@code i} solve a "two sum to {@code -nums[i]}" sub-problem over the remaining suffix using
 * a two-pointer scan. Sorting makes the sum monotonic with respect to pointer movement, which
 * lets each sub-problem run in linear time, giving an overall {@code O(n^2)} time cost (plus
 * {@code O(n log n)} for the sort).</p>
 */
public class ThreeSum {

    private final boolean optimized;

    /* package */ ThreeSum(boolean optimized) {
        this.optimized = optimized;
    }

    public List<List<Integer>> threeSum(int[] nums) {
        return optimized ? threeSumOptimized(nums) : threeSumHashSet(nums);
    }

    /**
     * Two-pointer solution that relies on a {@link HashSet} to remove duplicate triplets.
     *
     * <p>After sorting, the outer loop fixes the smallest element {@code nums[i]}. Two pointers
     * then converge from the ends of the suffix: {@code j} just after {@code i}, and {@code k} at
     * the end. Because the array is sorted, the running sum moves predictably:</p>
     * <ul>
     *   <li>sum {@code > 0} &rarr; decrement {@code k} to shrink the sum;</li>
     *   <li>sum {@code < 0} &rarr; increment {@code j} to grow the sum;</li>
     *   <li>sum {@code == 0} &rarr; record the triplet and move <em>both</em> pointers inward.</li>
     * </ul>
     *
     * <p>Moving both pointers on a match cannot skip a valid triplet: with {@code j} fixed, any
     * larger {@code k} value pushes the sum above 0, and with {@code k} fixed, any larger
     * {@code j} value does the same. The only combinations "skipped" are ones with identical
     * values (e.g. {@code nums[j + 1] == nums[j]}), which would be duplicate triplets. The
     * {@code HashSet} absorbs those duplicates, so no explicit skip logic is required.</p>
     *
     * @param nums the input array (reordered in place by sorting)
     * @return all unique zero-sum triplets
     */
    public List<List<Integer>> threeSumHashSet(int[] nums) {
        Set<List<Integer>> resultList = new HashSet<>();
        Arrays.sort(nums); // -4. -1, -1, 0, 1, 2
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            int j = i + 1, k = n - 1;
            while (j < k) {
                int currSum = nums[i] + nums[j] + nums[k];
                if (currSum == 0) {
                    resultList.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    // we can now move both left and right pointer - since the array is sorted,
                    // moving only one will only yield 0 for the same value as the previous. 
                    // e.g. nums[j + 1] > nums[j] => new currSum > 0, and
                    // nums[k - 1] < nums[k] => new currSum < 0
                    j++;
                    k--;
                } else if (currSum > 0) {
                    k--;
                } else {
                    j++;
                }
            }
        }

        return new ArrayList<>(resultList);
    }

    /**
     * Two-pointer solution that avoids a {@link HashSet} by skipping duplicate values explicitly.
     *
     * <p>This trades the hashing overhead of {@link #threeSumHashSet(int[])} for manual
     * de-duplication, appending directly to a {@link ArrayList}. Two prunings keep it fast:</p>
     * <ul>
     *   <li>the outer loop stops as soon as {@code nums[i] > 0}, since a sorted array cannot form
     *       a zero sum once the smallest chosen element is positive;</li>
     *   <li>after each match, the {@code j}/{@code k} pointers and the outer {@code i} index skip
     *       over runs of equal values so the same triplet is never emitted twice.</li>
     * </ul>
     *
     * @param nums the input array (reordered in place by sorting)
     * @return all unique zero-sum triplets
     */
    public List<List<Integer>> threeSumOptimized(int[] nums) {
        List<List<Integer>> resultList = new ArrayList<>();
        Arrays.sort(nums); // -4. -1, -1, 0, 1, 2
        int n = nums.length;
        int lastNumI;
        for (int i = 0; i < n - 2 && nums[i] <= 0; i++) {
            int numI = nums[i];
            lastNumI = numI;
            int j = i + 1, k = n - 1;
            while (j < k) {
                int numJ = nums[j], numK = nums[k];
                int currSum = numI + numJ + numK;
                if (currSum == 0) {
                    resultList.add(Arrays.asList(numI, numJ, numK));
                    while (j < n - 1 && nums[j + 1] == numJ) { // skip the dupes on left/low side
                        // replacing n - 1 with k above should make it faster, but on LeetCode it's the opposite
                        j++;
                    }
                    j++; // one more time, to move onto the next value
                    while (k > 1 && nums[k - 1] == numK) { // skip the dupes on the right / high side
                        // replacing 1 with j above should make it faster, but on LeetCode it's the opposite
                        k--;
                    }
                    k--; // go left / decrease once more
                } else if (currSum > 0) {
                    k--; // go left / decrease
                } else {
                    j++; // go right / increase
                }
            }
            while (i < n - 2 && nums[i + 1] == lastNumI) i++;
        }

        return resultList;
    }
}
