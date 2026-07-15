package org.callatis.study.streams;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MedianFinderTest {

    private static final double DELTA = 1e-5;

    private final int size;

    public MedianFinderTest(int size) {
        this.size = size;
    }

    @Parameters(name = "size={0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            {0},
            {101}
        });
    }

    @Test
    public void testExample1() {
        MedianFinder medianFinder = new MedianFinder(size);
        medianFinder.addNum(1);    // arr = [1]
        medianFinder.addNum(2);    // arr = [1, 2]
        assertEquals(1.5, medianFinder.findMedian(), DELTA); // (1 + 2) / 2
        medianFinder.addNum(3);    // arr = [1, 2, 3]
        assertEquals(2.0, medianFinder.findMedian(), DELTA);
    }

    @Test
    public void testExample2() {
        MedianFinder medianFinder = new MedianFinder(size);

        if (size > 0) {
            // The counting branch indexes nums[num]; negative values are out of
            // bounds, so the first negative addNum must throw.
            try {
                medianFinder.addNum(-1);
                fail("Expected ArrayIndexOutOfBoundsException for negative num when size=" + size);
            } catch (ArrayIndexOutOfBoundsException expected) {
                // expected
            }
            return;
        }

        medianFinder.addNum(-1);   // arr = [-1]
        assertEquals(-1.0, medianFinder.findMedian(), DELTA);
        medianFinder.addNum(-2);   // arr = [-2, -1]
        assertEquals(-1.5, medianFinder.findMedian(), DELTA); // (-2 + -1) / 2
        medianFinder.addNum(-3);   // arr = [-3, -2, -1]
        assertEquals(-2.0, medianFinder.findMedian(), DELTA);
        medianFinder.addNum(-4);   // arr = [-4, -3, -2, -1]
        assertEquals(-2.5, medianFinder.findMedian(), DELTA); // (-3 + -2) / 2
        medianFinder.addNum(-5);   // arr = [-5, -4, -3, -2, -1]
        assertEquals(-3.0, medianFinder.findMedian(), DELTA);
    }
}
