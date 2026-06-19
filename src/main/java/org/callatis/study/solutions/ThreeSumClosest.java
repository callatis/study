package org.callatis.study.solutions;

public class ThreeSumClosest {
    public int threeSumClosest(int[] nums, int target) {
        int minSum = target-Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    // System.out.println("Evaluating "+ i + ", " + j + ", " + k);
                    int newSum = nums[i] + nums[j] + nums[k];
                    if (Math.abs(target - minSum) > Math.abs(target - newSum)) {
                        minSum = newSum;
                        // System.out.println("Found " + i + ", " + j + ", " + k);
                    }
                }
            }
        }

        return minSum;
    }
}
