package org.callatis.study.strings;

import org.callatis.study.utils.Pair;

public class LongestPalindromeSubstring {

    private int length(Pair<Integer> pair) {
        return pair.y - pair.x + 1;
    }

    private Pair<Integer> checkPalindrome(String s, int left, int right) {
        int len = s.length();
        while (left >= 0 && right < len) {
            if (s.charAt(left) == s.charAt(right)) {
                left--;
                right++;
            } else {
                break;
            }
        }

        return new Pair<>(left + 1, right - 1);
    }

    public String longestPalindrome(String s) { // s = "ccc"
        if (s.length() <= 1) return s;
        int end = 1;
        int len = s.length();
        Pair<Integer> longestPalindrome = new Pair<>(0, 0);
        while (end < len) { // end = 1
            int c = s.charAt(end);
            int start = -1;
            if (c == s.charAt(end - 1)) {
                start = end - 1;
                while (end < len - 1 && c == s.charAt(end + 1)) {
                    end++;
                }
            } else if (end > 1 && c == s.charAt(end - 2)) {
                start = end - 2;
            }
            if (start >= 0) {
                Pair<Integer> palindrome = checkPalindrome(s, start, end);
                if (length(longestPalindrome) < length(palindrome)) {
                    longestPalindrome = palindrome;
                }
            }
            end++;
        }

        return s.substring(longestPalindrome.x, longestPalindrome.y + 1);
    }
}
