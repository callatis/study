package org.callatis.study.arrays;

public class RemoveDuplicatesFromSorted {

    private final boolean useV2;

    public RemoveDuplicatesFromSorted() {
        this(true);
    }

    public RemoveDuplicatesFromSorted(boolean useV2) {
        this.useV2 = useV2;
    }

    public int removeDuplicates(int[] nums) { // [1, 1, 2] // [0, 0, 1, 1, 1, 2, 2, 3, 3, 4]
        return useV2 ? removeDuplicatesV2(nums) : removeDuplicatesV1(nums);
    }

    private int removeDuplicatesV1(int[] nums) {
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

    private int removeDuplicatesV2(int[] nums) {
        int len = nums.length; // ??
        int k = 0;
        for (int i = 1; i < len; i++) {
            if (nums[i] > nums[k]) {
                nums[++k] = nums[i];
            }
        }

        return k + 1;
    }
}
