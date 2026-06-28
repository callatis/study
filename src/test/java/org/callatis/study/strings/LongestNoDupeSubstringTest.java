package org.callatis.study.strings;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class LongestNoDupeSubstringTest {

    private final LongestNoDupeSubstring longestNoDupeSubstring;

    public LongestNoDupeSubstringTest(boolean optimized) {
        this.longestNoDupeSubstring = new LongestNoDupeSubstring(optimized);
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
    public void testExample5Au() {
        // Input: s = "au"
        // Output: 2
        int expected = 2;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("au"));
    }

    @Test
    public void testExample6Dvdf() {
        // Input: s = "dvdf"
        // Output: 3
        int expected = 3;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("dvdf"));
    }

    @Test
    public void testExample7Jbpnbwwd() {
        // Input: s = "jbpnbwwd"
        // Output: 4
        int expected = 4;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("jbpnbwwd"));
    }

    @Test
    public void testExample9Bpfbhmipx() {
        // Input: s = "bpfbhmipx"
        // Output: 7
        // Explanation: "fbhmipx" (indices 2-8) has no duplicates
        int expected = 7;

        assertEquals(expected, longestNoDupeSubstring.lengthOfLongestSubstring("bpfbhmipx"));
    }
}