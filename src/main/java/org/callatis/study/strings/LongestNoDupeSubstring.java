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
        int i = 0, len = s.length(), start = 0, result = 0; // len = 9
        Map<Character, Integer> map = new HashMap<>();
        // set = []
        while (i < len) { // i = 0
            for (int j = i; j < len; j++) { // j = 3
                char c = s.charAt(j); // c = b
                if (map.containsKey(c)) { // yes
                    // stop
                    // check if we found a larger substring
                    result = Math.max(result, map.size()); // result = 3
                    // find the index we found the previous occurrence
                    int jj = map.get(c); // jj = 0
                    // clear the map elements up until jj
                    for (int k = start; k < jj; k++) { // k = 
                        map.remove(s.charAt(k)); // set: 
                    }
                    start = jj + 1; // start = 1
                    // replace the position with the current one
                    map.put(c, j); // set: [b = 3, p = 1, f = 2]
                    // start next iter from right after where the j char appeared before
                    i = j + 1; // i = 4
                    break;
                } else {
                    map.put(c, j); // set: [b = 3, p = 1, f = 2]
                }
                // if we reached the end of the string
                if (j == len - 1) { // no
                    result = Math.max(result, j - i + 1);
                    break;
                }
            }
        }

        return result;
    }

}
