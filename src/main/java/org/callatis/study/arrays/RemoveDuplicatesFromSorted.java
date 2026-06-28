package org.callatis.study.arrays;

public class RemoveDuplicatesFromSorted {
    
    public int removeDuplicates(int[] nums) { // [1, 1, 2] // [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]
        int len = nums.length; // ??
        int k = 0; // insertion point - where first dupe is found
        int i = 0; // running index
        while (i < len) { // i = , k = 
            int j = i + 1; // j = 
            while (j < len && nums[i] == nums[j]) { // ??
                j++; // j = 
            }
            // i < j < nums.length, j is the first index
            // with nums[j] > nums[i]
            nums[k++] = nums[i]; // nums = [??], k = 
            i = j; // i = 
        }

        return k; 
    }

}
