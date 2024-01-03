package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PalidromeGenerator {
    private Map<Integer, List<Long>> paliCache = new HashMap<>();
// discard numbers > # palindromes, which is 9 * 10 ^ (len - 1)
// decrement num after each generatePalindromes with the size of the list returned
    public long[] kthPalindrome(int[] queries, int intLength) {
    	SortedMap<Long, Long> resultMap = new TreeMap<>();
        int num = max(queries, intLength, resultMap);
        List<Long> paliList = generatePalindromes(intLength, num);
        // System.out.println("**** Generated " + paliList.size()
        //     + " palindromes of length " + intLength 
        //     + ": \n\t" + paliList);
        List<Long> resultList = new ArrayList<>();
        for (int query : queries) {
            if (query <= paliList.size()) {
                resultList.add(paliList.get(intLength == 1 ? query : query - 1));
            } else {
                resultList.add(-1L);
            }
        }

        long[] results = new long[resultList.size()];
        int i = 0;
        for (long result : resultList) {
            results[i++] = result;
        }
        return results;
    }

    private long[] DIGITS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

    private int max(int[] queries, int len, Map<Long, Long> map) {
        int max = -1;
        double maxPaliesDbl = 9 * Math.pow(10, len - 2);
        long maxPalies = (long) maxPaliesDbl;
        System.out.println("~~~~ Max Palies = " + maxPalies);
        for (int query : queries) {
        	map.put((long) query, -1L);
            if (query <= maxPalies) {
            	if (query > max) max = query;
            } else {
            	System.out.println("Discarding query " + query 
            			+ " as it's larger than the max # " + len 
            			+ " palindromes = " + maxPalies);
            }
            	
        }
        System.out.println("Found max = " + max);
        
        return max;
    }

    private List<Long> generatePalindromes(int len, int num) {
        List<Long> paliList = this.paliCache.get(len);

        if (paliList == null) {
            System.out.println("Computing palindromes of length " + len);
            paliList = computePalindromes(len, num);
            this.paliCache.put(len, paliList);
        }

        return paliList;
    }

    /**
     * @param len
     * @param num
     * @return
     */
    private List<Long> computePalindromes(int len, int num) {
        // System.out.println("Generating " + num + " palindromes of length " + len);
        List<Long> paliList = new ArrayList<>();
        if (len == 1) {
            System.out.println("Computing base case len == 1");
            for (Long digit : DIGITS) {
                paliList.add(digit);
            }
            return paliList;
        } else if (len == 2) {
            System.out.println("Computing base case len == 2");
            for (Long digit : DIGITS) {
                if (digit > 0) {
                    paliList.add(digit * 10 + digit);
                }
            }
            return paliList;
        } else if (len == 0) {
            System.out.println("Computing base case len == 0");
            paliList.add(0L);
            return paliList;
        }
        boolean stillGoing = true; 
        for (long digit : DIGITS) {
            if (digit == 0) continue; // a number cannot start with 0
            // System.out.println("\tGenerating palindromes braced by " + digit);
            int theLen = len % 2 == 0 ? 0 : 1;
            // get palindromes of lesser length
            while (stillGoing && theLen < len - 1) {
                List<Long> babyPaliList = generatePalindromes(theLen, num);
                // System.out.println("\tGot baby palies of length " + theLen 
                    // + ": " + babyPaliList);
                for (long babyPali : babyPaliList) {
                    // System.out.println("\t\tWrapping " + babyPali);
                    long babyTenPow = (long) Math.pow(10, (len - theLen) / 2);
                    long elevBabyPali = babyPali * babyTenPow;
                    // System.out.println("\t\tElevated baby pali = " + elevBabyPali);
                    long tenPow = (long) Math.pow(10, len - 1);
                    long pali = digit * tenPow + elevBabyPali + digit;
                    // System.out.println("\t\tGenerated pali #" + num + " = " + pali);
                    paliList.add(pali);
                    num--;
//                    System.out.println("Current paliList = " + paliList);
                    if (num == 0) {
                        System.out.println("\t\tReached max " + num);
                        stillGoing = false;
                        break;
                    }
                }
                theLen += 2;
            }
            if (!stillGoing) break;
        }
        System.out.println("*********************************************");
        System.out.println("Computed paliList for " + len + ", " + num
        ); 
            // + " = " + paliList);
        System.out.println("*********************************************");
        
        return paliList;
    }
    
    public static void main(String[] args) {
    	PalidromeGenerator sol = new PalidromeGenerator();
    	// Case 3
//    	int[] queries = { 659108523,547414705,89,81,4,346605675,12,355852667,34,781116116 };
//    	int len = 7;
    	// Case 1
//    	int[] queries = { 1,2,3,4,5,90 };
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
    	int[] queries = { 3790 };
    	int len = 7;
    	long[] palies = sol.kthPalindrome(queries, len);
    	List<Long> paliesList = new ArrayList<>(palies.length);
    	for (long pali : palies) {
    		paliesList.add(pali);
    	}
		System.out.println("Generated: \n\t" + paliesList);
    }

}