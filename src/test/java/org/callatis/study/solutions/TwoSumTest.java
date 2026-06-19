package org.callatis.study.solutions;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;

public class TwoSumTest {

    private TwoSum twoSum;

    @Before
    public void setUp() {
        twoSum = new TwoSum();
    }

    @Test
    public void testExample1() {
        // Input: nums = [2,7,11,15], target = 9
        // Output: [0,1]
        // Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].
        int[] nums = {2, 7, 11, 15};
        int target = 9;
        int[] expected = {0, 1};
        int[] result = twoSum.twoSum(nums, target);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testExample2() {
        // Input: nums = [3,2,4], target = 6
        // Output: [1,2]
        int[] nums = {3, 2, 4};
        int target = 6;
        int[] expected = {1, 2};
        int[] result = twoSum.twoSum(nums, target);
        assertArrayEquals(expected, result);
    }

    @Test
    public void testExample3() {
        // Input: nums = [3,3], target = 6
        // Output: [0,1]
        int[] nums = {3, 3};
        int target = 6;
        int[] expected = {0, 1};
        int[] result = twoSum.twoSum(nums, target);
        assertArrayEquals(expected, result);
    }
}
