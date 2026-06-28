package org.callatis.study.arrays;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class RemoveDuplicatesFromSortedTest {

    private final RemoveDuplicatesFromSorted solution;

    public RemoveDuplicatesFromSortedTest(boolean useV2) {
        solution = new RemoveDuplicatesFromSorted(useV2);
    }

    @Parameters(name = "useV2={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            {false},
            {true}
        });
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
