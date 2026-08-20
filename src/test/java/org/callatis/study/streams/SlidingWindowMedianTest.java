package org.callatis.study.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("TODO: Re-enable after SlidingWindowMedian is implemented")
public class SlidingWindowMedianTest {

    private static final double DELTA = 1e-5;

    @Test
    public void testExample1() {
        SlidingWindowMedian solution = new SlidingWindowMedian();
        int[] nums = {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        double[] expected = {1.00000, -1.00000, -1.00000, 3.00000, 5.00000, 6.00000};
        assertArrayEquals(expected, solution.medianSlidingWindow(nums, k), DELTA);
    }

    @Test
    public void testExample2() {
        SlidingWindowMedian solution = new SlidingWindowMedian();
        int[] nums = {1, 2, 3, 4, 2, 3, 1, 4, 2};
        int k = 3;
        double[] expected = {2.00000, 3.00000, 3.00000, 3.00000, 2.00000, 3.00000, 2.00000};
        assertArrayEquals(expected, solution.medianSlidingWindow(nums, k), DELTA);
    }

    // Example 3 in the Markdown provides only the input (nums of length 100000,
    // k = 50000) with no expected output. With no expected values to compare
    // against, this test asserts only the median array length (nums.length - k + 1).
    @Test
    public void testExample3Length() throws IOException {
        SlidingWindowMedian solution = new SlidingWindowMedian();
        int[] nums = readExample3();
        int k = 50000;
        assertEquals(100000, nums.length);
        double[] result = solution.medianSlidingWindow(nums, k);
        assertEquals(nums.length - k + 1, result.length);
    }

    private static int[] readExample3() throws IOException {
        String csv;
        try (InputStream in = SlidingWindowMedianTest.class.getResourceAsStream(
                "SlidingWindowMedian.example3.txt")) {
            if (in == null) {
                throw new IOException("Missing test resource: SlidingWindowMedian.example3.txt");
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            csv = sb.toString();
        }
        String[] tokens = csv.split(",");
        int[] nums = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            nums[i] = Integer.parseInt(tokens[i].trim());
        }
        return nums;
    }

}
