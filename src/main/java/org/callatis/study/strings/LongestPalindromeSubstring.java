package org.callatis.study.strings;

public class LongestPalindromeSubstring {

    class IntPair {
        
        int left;
        int right;

        IntPair(int left, int right) {
            this.left = left;
            this.right = right;
        }

        public int length() {
            return right - left + 1;
        }
    }

    private IntPair checkPalindrome(String s, int left, int right) {
        int len = s.length();
        while (left >= 0 && right < len) {
            if (s.charAt(left) == s.charAt(right)) {
                left--;
                right++;
            } else {
                break;
            }
        }

        return new IntPair(left + 1, right - 1);
    }

    public String longestPalindrome(String s) { // s = "ccc"
        if (s.length() <= 1) return s;
        int end = 1;
        int len = s.length();
        IntPair longestPalindrome = new IntPair(0, 0);
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
                IntPair palindrome = checkPalindrome(s, start, end);
                if (longestPalindrome.length() < palindrome.length()) {
                    longestPalindrome = palindrome;
                }
            }
            end++;
        }

        return s.substring(longestPalindrome.left, longestPalindrome.right - longestPalindrome.left + 1);
    }
}
