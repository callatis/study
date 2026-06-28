package org.callatis.study.strings;

import java.util.HashMap;
import java.util.Map;

public class LongestNoDupeSubstring {

    private final boolean optimized;

    public LongestNoDupeSubstring(boolean optimized) {
        this.optimized = optimized;
    }

    public int lengthOfLongestSubstring(String s) { // s = 
        if (optimized) {
            return lengthOfLongestSubstringOptimized(s);
        }
        return lengthOfLongestSubstringOriginal(s);
    }

    private int lengthOfLongestSubstringOriginal(String s) { // s = 
        if (s.length() <= 1) return s.length();
        int i = 0, len = s.length(), result = 0;
        Map<Character, Integer> map = new HashMap<>();
        while (i < len) { // i = 0
            for (int j = i; j < len; j++) { // j = 
                char c = s.charAt(j); // c = 
                if (map.containsKey(c)) { // ??
                    // stop 
                    // check if we found a larger substring
                    result = Math.max(result, j - i); // result = 
                    // start next iter from the previous index where the j char appeared
                    i = map.get(c) + 1; // ?? + 1 = ??
                    // we resume from right after the previous position of the j char
                    map.clear(); // set: [?? = ??, ...]
                    break;
                }
                map.put(c, j); // set: [?? = ??, ...]
                // if we reached the end of the string
                if (j == len - 1) {
                    result = Math.max(result, j - i + 1);
                    break;
                }
            }
        }

        return result;
    }

    private int lengthOfLongestSubstringOptimized(String s) {
        if (s.length() <= 1) return s.length();
        int i = 0, len = s.length(), start = 0, result = 0; // len = 
        int[] char2Idx = new int[128];
        // set = []
        while (i < len) { // i = 
            for (int end = i; end < len; end++) { // end = 
                char cc = s.charAt(end); // c = 
                int c = (int) cc;
                if (char2Idx[c] > 0) { // ??
                    // stop
                    // check if we found a larger substring
                    result = Math.max(result, end - start); // result = 
                    // find the index we found the previous occurrence
                    int former = char2Idx[c] - 1; // former = 
                    // clear the map elements up until jj
                    for (int k = start; k < former; k++) { // k = 
                        char2Idx[(int) s.charAt(k)] = 0; // set: 
                    }
                    start = former + 1; // start = 
                    // replace the position with the current one
                    char2Idx[c] = end + 1; // set: []
                    // start next iter from right after where the j char appeared before
                    i = end + 1; // i = 
                    break;
                } else {
                    char2Idx[c] = end + 1; // set: []
                }
                // if we reached the end of the string
                if (end == len - 1) { // ??
                    result = Math.max(result, end - start + 1);
                    break;
                }
            }
        }

        return result;
    }

}
