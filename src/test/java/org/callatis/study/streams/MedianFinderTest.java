package org.callatis.study.streams;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MedianFinderTest {

    private static final double DELTA = 1e-5;

    @Test
    public void testExample1() {
        MedianFinder medianFinder = new MedianFinder();
        medianFinder.addNum(1);    // arr = [1]
        medianFinder.addNum(2);    // arr = [1, 2]
        assertEquals(1.5, medianFinder.findMedian(), DELTA); // (1 + 2) / 2
        medianFinder.addNum(3);    // arr = [1, 2, 3]
        assertEquals(2.0, medianFinder.findMedian(), DELTA);
    }
}
