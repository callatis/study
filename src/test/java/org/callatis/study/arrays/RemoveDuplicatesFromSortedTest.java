package org.callatis.study.arrays;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class RemoveDuplicatesFromSortedTest {

    private RemoveDuplicatesFromSorted solution;

    @Before
    public void setUp() {
        solution = new RemoveDuplicatesFromSorted();
    }

    @Test
    public void testExample1() {
        int[] nums = {1, 1, 2};
        int[] expectedNums = {1, 2};

        int k = solution.removeDuplicates(nums);

        assertEquals(expectedNums.length, k);
        assertArrayEquals(expectedNums, Arrays.copyOf(nums, k));
    }

    @Test
    public void testExample2() {
        int[] nums = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        int[] expectedNums = {0, 1, 2, 3, 4};

        int k = solution.removeDuplicates(nums);

        assertEquals(expectedNums.length, k);
        assertArrayEquals(expectedNums, Arrays.copyOf(nums, k));
    }
}
