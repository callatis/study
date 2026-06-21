package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        return threeSumOptimized(nums);
    }

    public List<List<Integer>> threeSumHashSet(int[] nums) {
        Set<List<Integer>> resultList = new HashSet<>();
        Arrays.sort(nums); // -4. -1, -1, 0, 1, 2
        int n = nums.length;
        for (int i = 0; i < n - 2; i++) {
            int j = i + 1, k = n - 1;
            while (j < k) {
                int currSum = nums[i] + nums[j] + nums[k];
                if (currSum == 0) {
                    resultList.add(Arrays.asList(nums[i], nums[j], nums[k]));
                    j++;
                    k--;
                } else if (currSum > 0) {
                    k--;
                } else {
                    j++;
                }
            }
        }

        return new ArrayList<>(resultList);
    }

    public List<List<Integer>> threeSumOptimized(int[] nums) {
        List<List<Integer>> resultList = new ArrayList<>();
        Arrays.sort(nums); // -4. -1, -1, 0, 1, 2
        int n = nums.length;
        int lastNumI = 1;
        for (int i = 0; i < n - 2 && nums[i] <= 0; i++) {
            while (i < n - 2 && nums[i] == lastNumI) i++;
            int numI = nums[i];
            // check if we haven't gotten above 0
            if (nums[i] > 0) { // short-circuit
                return resultList;
            }
            lastNumI = numI;
            int j = i + 1, k = n - 1;
            while (j < k) {
                int numJ = nums[j], numK = nums[k];
                int currSum = numI + numJ + numK;
                if (currSum == 0) {
                    resultList.add(Arrays.asList(numI, numJ, numK));
                    while (j < n - 1 && nums[j + 1] == numJ) {
                        j++;
                    }
                    j++; // one more time, to move onto the next value
                    while (k > 1 && nums[k - 1] == numK) {
                        k--;
                    }
                    k--; // one more time, to move onto the next value
                } else if (currSum > 0) {
                    while (k > 1 && nums[k - 1] == numK) {
                        k--;
                    }
                    k--; // one more time, to move onto the next value
                } else {
                    while (j < n - 1 && nums[j + 1] == numJ) {
                        j++;
                    }
                    j++; // one more time, to move onto the next value
                }
            }
        }

        return resultList;
    }
}
