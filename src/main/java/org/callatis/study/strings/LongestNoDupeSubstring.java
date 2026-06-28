package org.callatis.study.strings;

import java.util.HashMap;
import java.util.Map;

public class LongestNoDupeSubstring {

    public int lengthOfLongestSubstring(String s) { // s = 
        if (s.length() <= 1) return s.length();
        int i = 0, len = s.length(), result = 0;
        Map<Character, Integer> set = new HashMap<>();
        while (i < len) { // i = 0
            for (int j = i; j < len; j++) { // j = 
                char c = s.charAt(j); // c = 
                if (set.containsKey(c)) { // ??
                    // stop 
                    // check if we found a larger substring
                    result = Math.max(result, j - i); // result = 
                    // start next iter from the previous index where the j char appeared
                    i = set.get(c) + 1; // ?? + 1 = ??
                    // we resume from right after the previous position of the j char
                    set.clear(); // set: [?? = ??, ...]
                    break;
                }
                set.put(c, j); // set: [?? = ??, ...]
                // if we reached the end of the string
                if (j == len - 1) {
                    result = Math.max(result, j - i + 1);
                    break;
                }
            }
        }

        return result;
    }

}
