package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.List;

public class KthFixedLengthPalindrome {
    public long[] kthPalindrome(int[] queries, int intLength) {
    	long[] results = new long[queries.length];
    	for (int i = 0; i < queries.length; i++) {
    		results[i] = genPali(queries[i], intLength);
    	}
    	
    	return results;
    }

    private long genPali(int N, int len) {
    	boolean odd = len % 2 > 0;

    	int n = len / 2; // len = 2n or 2n+1
    	// the order of the palindromes is the order of their first (n+1) digits, since there's exactly  
    	// 1 palindrome for each such sequence, and the first digits are more "relevant";
    	// e.g. 1234 < 2345 => 1234321 < 2345432
    	if (!odd) n--; 
    	N += Math.pow(10, n);
    	// Furthermore, we have 10 digits for all inner positions, but only 9 for the first one (no number
    	// starts with 0). We level the algo by "adding" all those pseudo-numbers and advancing the pali
    	// sequence number with 2^n (there are n pseudo-numbers starting with 0).
    	
    	return computeNthPalindrome(N - 1, n, odd);
//		return odd ?
//				computeNthOddPalindrome(N - 1, n) : computeNthEvenPalindrome(N - 1, n);
	}

	private long computeNthPalindrome(long N, int n, boolean odd) { 
		long nthPow = (long) Math.pow(10,  n);
		if (N >= nthPow * 10) {
			return -1;
		}
		if (n == 0) {
			return N  + (odd ? 0 : N * 10);
		}
		
		long firstDigit = N / nthPow;
		long rightDigits = N % nthPow;
		long innerPali = computeNthPalindrome(rightDigits, n - 1, odd); 
		
		return firstDigit * nthPow *nthPow * (odd ? 1 : 10) + 10 * innerPali + firstDigit;
	}

	private long computeNthOddPalindrome(long N, int n) {
		long nthPow = (long) Math.pow(10,  n); // 10
		if (N >= nthPow * 10) { // 23 <= 100
			return -1;
		}
		if (n == 0) return N;
		
		long firstDigit = N / nthPow; // 23 / 10 = 2
		long rightDigits = N % nthPow; // 23 % 10 = 3
		// f(2, 0) = 2
		// f(92, 1) = 9 * 100 + 2 * 10 + 9
		long innerPali = computeNthOddPalindrome(rightDigits, n - 1); // 3, 0 
		
		return firstDigit * nthPow * nthPow + 10 * innerPali + firstDigit; // 2 * 10 * 10 * 10 + 33 * 10 + 2
	}

	private long computeNthEvenPalindrome(long N, int n) { // N = 23, n = 1
		long nthPow = (long) Math.pow(10,  n);
		if (N >= nthPow * 10) {
			return -1;
		}
		if (n == 0) {
			return N * 10 + N;
		}
		
		long firstDigit = N / nthPow;
		long rightDigits = N % nthPow;
		long innerPali = computeNthEvenPalindrome(rightDigits, n - 1); 
		
		return firstDigit * nthPow *nthPow * 10 + 10 * innerPali + firstDigit;
	}

	public static void main(String[] args) {
    	KthFixedLengthPalindrome sol = new KthFixedLengthPalindrome();
    	// Case 3
//    	int[] queries = { 659108523,547414705,89,81,4,346605675,12,355852667,34,781116116 };
//    	int len = 7;
    	// Case 1
//    	int[] queries = { 1,2,3,4,5,10, 90 };
//    	int len = 3;
    	// My Case 1
//    	int[] queries = { 1,2,3,4,5 };
//    	int len = 5;
    	// Another rejection case:
//    	int[] queries = { 105848303,57,8,513489687,43,21,75,15,99517488,85,19,947419916,416364456 };
//    	int len = 9;
    	// Another rejection case: 
//    	int[] queries = { 83,35,5,474568655,518949853,178697884,121250956,434016234,54,20 };
//    	int len = 10;
    	// Another rejection case: 
//    	int[] queries = {475098318,62,457771600,85,476799241,23,73,600686743,58,264628531,26,25,9};
//    	int len = 10;
//    	int[] queries = { 3792 };
//    	int len = 7;
//    	int[] queries = { 10 };
//    	int len = 3;
//    	int[] queries = { 1, 10, 11, 100, 101, 127, 900 };
//    	int len = 6;
//    	int[] queries = { 2,201429812,8,520498110,492711727,339882032,462074369,9,7,6 };
//    	int len = 1;
//    	int[] queries = { 392015495,5,4,1,425320571,565971690,3,7,6,3,506222280,468075092,5 };
//    	int len = 2;
    	int[] queries = { 10 };
    	int len = 1;
    	long[] palies = sol.kthPalindrome(queries, len);
    	List<Long> paliesList = new ArrayList<>(palies.length);
    	for (long pali : palies) {
    		paliesList.add(pali);
    	}
		System.out.println("Generated: \n\t" + paliesList);
    }

}