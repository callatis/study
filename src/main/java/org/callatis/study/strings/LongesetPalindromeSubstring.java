package org.callatis.study.strings;

public class LongesetPalindromeSubstring {

    private String checkPalindrome(String s, int left, int right) {
        int len = s.length();
        while (left <= right && left >= 0 && right < len) {
            if (s.charAt(left) == s.charAt(right)) {
                left--;
                right++;
            } else {
                break;
            }
        }
        left++;
        right--;

        StringBuilder sb = new StringBuilder();
        for (int i = left; i <= right; i++) {
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    public String longestPalindrome(String s) { // s = "ccc"
        if (s.length() <= 1) return s;
        int end = 1;
        int len = s.length();
        String longestPalindrome = s.substring(0, 1);
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
                String palindrome = checkPalindrome(s, start, end);
                if (longestPalindrome.length() < palindrome.length()) {
                    longestPalindrome = palindrome;
                }
            }
            end++;
        }

        return longestPalindrome;
    }
    
}
