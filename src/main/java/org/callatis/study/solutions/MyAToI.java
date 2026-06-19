package org.callatis.study.solutions;

public class MyAToI {

       private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public int myAtoi(String s) {
        if (s.length() == 0) return 0;
        
        final long MIN_INT = (long) -Math.pow(2, 31);
        final long MAX_INT = (long) Math.pow(2, 31) - 1;
        int i = 0;
        long result = 0L;
        while (i < s.length() && s.charAt(i) == ' ') {
            i++;
        }
        if (i >= s.length()) return 0;
        
        boolean positive = true;
        if (s.charAt(i) == '-') {
            positive = false;
            i++;
        } else if (s.charAt(i) == '+') {
            i++;
        }
        while (i < s.length() && isDigit(s.charAt(i))) {
            result *= 10;
            result += s.charAt(i) - '0';
            i++;
            if (positive && result > MAX_INT) {
                return (int) MAX_INT;
            }
            if (!positive && -MIN_INT - result < 0) {
                return (int) MIN_INT;
            }
        }

        return (int) result * (positive ? 1 : -1);
    }

}