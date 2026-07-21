package org.callatis.study.solutions;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Parameterized tests for {@link ThreeSumClosest}. Each LeetCode example is executed against all
 * three implementations ({@code bruteForce}, {@code partialOptimized}, {@code optimized}) so they
 * are verified to agree on the same closest sum.
 */
@RunWith(Parameterized.class)
public class ThreeSumClosestTest {

    private final ThreeSumClosest threeSumClosest;

    public ThreeSumClosestTest(String implementation) {
        this.threeSumClosest = new ThreeSumClosest(implementation);
    }

    /**
     * Supplies the implementation identifier for each run; JUnit instantiates the class once per
     * entry, so every {@code @Test} below runs three times, once per strategy.
     *
     * @return the set of implementation identifiers to exercise
     */
    @Parameters(name = "implementation={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                {"bruteForce"},
                {"partialOptimized"},
                {"optimized"}
        });
    }

    /** Mixed signs: {@code -1 + 2 + 1 = 2} is the closest sum to target {@code 1}. */
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

    /** Degenerate all-zero input: the only triplet sums to {@code 0}, closest to target {@code 1}. */
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

    /** Exact match: {@code 0 + 1 + 2 = 3} is the only triplet, closest to target {@code 0}. */
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

    /** Duplicates and an exact hit: a triplet sums to {@code -2}, matching the target exactly. */
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
