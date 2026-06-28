package org.callatis.study.strings;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class LongestNoDupeSubstringTest {

    private LongestNoDupeSubstring longestNoDupeSubstring;

    @Before
    public void setUp() {
        longestNoDupeSubstring = new LongestNoDupeSubstring();
    }

    @Test
    public void testExample1() {
        // Input: s = "abcabcbb"
        // Output: 3
        int expected = 3;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("abcabcbb"));
    }

    @Test
    public void testExample2() {
        // Input: s = "bbbbb"
        // Output: 1
        int expected = 1;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("bbbbb"));
    }

    @Test
    public void testExample3() {
        // Input: s = "pwwkew"
        // Output: 3
        int expected = 3;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("pwwkew"));
    }

    @Test
    public void testExample4() {
        // Input: s = ""
        // Output: 0
        int expected = 0;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring(""));
    }

    @Test
    public void testExample5Space() {
        // Input: s = " "
        // Output: 1
        int expected = 1;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring(" "));
    }

    @Test
    public void testExample6Au() {
        // Input: s = "au"
        // Output: 2
        int expected = 2;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("au"));
    }
}