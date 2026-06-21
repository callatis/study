package org.callatis.study.solutions;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class ThreeSumClosestTest {

    private ThreeSumClosest threeSumClosest;

    @Before
    public void setUp() {
        threeSumClosest = new ThreeSumClosest();
    }

    @Test
    public void testExample1() {
        // Input: nums = [-1,2,1,-4], target = 1
        // Output: 2
        // Explanation: The sum that is closest to the target is 2. (-1 + 2 + 1 = 2).
        int[] nums = {-1, 2, 1, -4};
        int target = 1;
        int expected = 2;
        assertEquals(expected, threeSumClosest.threeSumClosest(nums, target));
    }

    @Test
    public void testExample2() {
        // Input: nums = [0,0,0], target = 1
        // Output: 0
        // Explanation: The sum that is closest to the target is 0. (0 + 0 + 0 = 0).
        int[] nums = {0, 0, 0};
        int target = 1;
        int expected = 0;
        assertEquals(expected, threeSumClosest.threeSumClosest(nums, target));
    }

    @Test
    public void testExample3() {
        // Input: nums = [0,1,2], target = 0
        // Output: 3
        // Explanation: The sum that is closest to the target is 3. (0 + 1 + 2 = 3).
        int[] nums = {0, 1, 2};
        int target = 0;
        int expected = 3;
        assertEquals(expected, threeSumClosest.threeSumClosest(nums, target));
    }

    @Test
    public void testExample4() {
        // Input: nums = [4,0,5,-5,3,3,0,-4,-5], target = -2
        // Output: -2
        int[] nums = {4, 0, 5, -5, 3, 3, 0, -4, -5};
        int target = -2;
        int expected = -2;
        assertEquals(expected, threeSumClosest.threeSumClosest(nums, target));
    }

}
