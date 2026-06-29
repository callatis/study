package org.callatis.study.strings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class LongestPalindromeSubstringTest {

    private LongestPalindromeSubstring longestPalindromeSubstring;

    @Before
    public void setUp() {
        longestPalindromeSubstring = new LongestPalindromeSubstring();
    }

    @Test
    public void testExample1() {
        // Input: s = "babad"
        // Output: "bab"
        // Explanation: "aba" is also a valid answer.
        String result = longestPalindromeSubstring.longestPalindrome("babad");

        assertEquals(3, result.length());
        assertTrue("bab".equals(result) || "aba".equals(result));
    }

    @Test
    public void testExample2() {
        // Input: s = "cbbd"
        // Output: "bb"
        String expected = "bb";

        assertEquals(expected, longestPalindromeSubstring.longestPalindrome("cbbd"));
    }

    @Test
    public void testExample3() {
        // Input: "a"
        // Output: "a"
        String expected = "a";

        assertEquals(expected, longestPalindromeSubstring.longestPalindrome("a"));
    }

    @Test
    public void testExample4() {
        // Input: "aa"
        // Output: "aa"
        String expected = "aa";

        assertEquals(expected, longestPalindromeSubstring.longestPalindrome("aa"));
    }

    @Test
    public void testExample5() {
        // Input: "ccc"
        // Output: "ccc"
        String expected = "ccc";

        assertEquals(expected, longestPalindromeSubstring.longestPalindrome("ccc"));
    }
}
