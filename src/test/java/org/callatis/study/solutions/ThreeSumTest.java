package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ThreeSumTest {

    private final ThreeSum threeSum;

    public ThreeSumTest(boolean optimized) {
        this.threeSum = new ThreeSum(optimized);
    }

    @Parameters(name = "optimized={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                {true},
                {false}
        });
    }

    @Test
    public void testExample1() {
        // Input: nums = [-1,0,1,2,-1,-4]
        // Output: [[-1,-1,2],[-1,0,1]]
        int[] nums = {-1, 0, 1, 2, -1, -4};

        List<List<Integer>> actual = threeSum.threeSum(nums);
        List<String> actualTriplets = normalize(actual);

        List<String> expectedTriplets = normalizeStrings(new ArrayList<>(Arrays.asList(
                "-1,-1,2",
                "-1,0,1"
        )));

        assertEquals(expectedTriplets, actualTriplets);
    }

    @Test
    public void testExample2() {
        // Input: nums = [0,1,1]
        // Output: []
        int[] nums = {0, 1, 1};

        List<List<Integer>> actual = threeSum.threeSum(nums);

        assertEquals(0, actual.size());
    }

    @Test
    public void testExample3() {
        // Input: nums = [0,0,0]
        // Output: [[0,0,0]]
        int[] nums = {0, 0, 0};

        List<List<Integer>> actual = threeSum.threeSum(nums);
        List<String> actualTriplets = normalize(actual);

        List<String> expectedTriplets = normalizeStrings(new ArrayList<>(Arrays.asList("0,0,0")));

        assertEquals(expectedTriplets, actualTriplets);
    }

    @Test
    public void testExample4() {
        // Input: nums = [0,0,0,0]
        // Output: [[0,0,0]]
        int[] nums = {0, 0, 0, 0};

        List<List<Integer>> actual = threeSum.threeSum(nums);
        List<String> actualTriplets = normalize(actual);

        List<String> expectedTriplets = normalizeStrings(new ArrayList<>(Arrays.asList("0,0,0")));

        assertEquals(expectedTriplets, actualTriplets);
    }

    private List<String> normalize(List<List<Integer>> triplets) {
        return normalizeStrings(triplets.stream()
                .map(triplet -> triplet.stream()
                        .sorted()
                        .map(String::valueOf)
                        .collect(Collectors.joining(",")))
            .collect(Collectors.toList()));
        }

        private List<String> normalizeStrings(List<String> values) {
        return values.stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
