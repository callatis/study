package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.List;

/**
 * Given an integer x, return true if x is a palindrome, and false otherwise.
 * 
 * @see <a href="https://leetcode.com/problems/palindrome-number/description/?submissionId=939216774">
 * LeetCode Palindrome Number</a>
 * 
 * <b>Note:</b> Comment the System.out.println if you want to have it run faster.  
 * 
 * @author mishe
 */
public class IntegerPalindrome {
	
		public static void main(String[] args) {
			if (args.length < 1) {
				throw new IllegalArgumentException("Needs at least one integer argument");
			}
			for (String arg : args) {
				try {
					int x = Integer.valueOf(arg);
					IntegerPalindrome obj = new IntegerPalindrome();
					System.out.println("IsPalindrome(" + arg + ") = " + obj.isPalindrome(x));
				} catch (NumberFormatException e) {
					System.err.println("Could not parse " + arg + " as int");
					e.printStackTrace();
				}
			}
		}
	
	    public boolean isPalindrome(int x) {
	        // return isStringPalindrome(x);        
	        // return isIntPalindrome(x);
	        return isIntArrPalindrome(toIntArr(x));
	    }

	    private Integer[] toIntArr(int x) {
	        if (x < 0) {
	            return null;
	        }
	        List<Integer> intList = new ArrayList<>();
	         int numChars = 1;
	        int y = x;
	        do {
	            int digit = y % 10;
	             System.out.println("Digit #" + numChars + " = " + digit);
	            intList.add(digit);
	            y = y / 10;
	             System.out.println("div 10 = " + y);
	             if (y > 0) numChars++;
	        } while (y > 0);

	        return intList.toArray(new Integer[intList.size()]);
	    }

	    private boolean isIntArrPalindrome(Integer[] intArr) {
	        if (intArr == null) return false;
	        int len = intArr.length;
	         System.out.println("Length = " + len);
	        for (int i = 0; i < len / 2; i++) {
	            int leftChar = intArr[i];
	            int j = len - i - 1;
	            int rightChar = intArr[j];
	             System.out.println("x[" + i + "] = " + leftChar
	                 + " vs. x[" + j + "] = " + rightChar);
	            if (leftChar != rightChar) return false;
	        }

	        return true;
	    }

	    private boolean isStringPalindrome(int x) {
	        String string = Integer.toString(x); 
	        return isPalindrome(string);
	    }

	    private boolean isPalindrome(String s) {
	        int len = s.length();
	        for (int i = 0; i < len / 2; i++) {
	            System.out.println("s[" + i + "] = " + s.charAt(i)
	                + " vs. s[" + (len - i - 1) + "] = " + s.charAt(len - i - 1));
	            if (s.charAt(i) != s.charAt(len - i - 1)) return false;
	        }

	        return true;
	    }

	    private int getNumChars(int x) {
	        int numChars = 1;
	        int y = x;
	        do {
	            y = y / 10;
	            System.out.println(" div 10 = " + y);
	            if (y > 0) numChars++; 
	        } while (y > 0);

	        return numChars;
	    }

	    private int charAt(int x, int i) {
	        int rightTenPow = (int) Math.pow(10, i);
	        System.out.println("rightTenPow(" + x + ", " + i + ")=" + rightTenPow);
	        int left = x / rightTenPow;
	        return left % 10;
	    }

	    private boolean isIntPalindrome(int x) {
	        if (x < 0) return false; 
	        int len = getNumChars(x);
	        System.out.println("Length = " + len);
	        for (int i = 0; i < len / 2; i++) {
	            int leftChar = charAt(x, i);
	            int j = len - i - 1;
	            int rightChar = charAt(x, j);
	            System.out.println("x[" + i + "] = " + leftChar
	                + " vs. x[" + j + "] = " + rightChar);
	            if (leftChar != rightChar) return false;
	        }

	        return true;
	    }

}