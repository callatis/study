package org.callatis.study.solutions;

import junit.framework.Assert;

/**
 * Canonical dynamic-programming solution for
 * {@link https://leetcode.com/problems/selling-pieces-of-wood/description/}.
 *
 * <p>Unlike {@link WoodSeller} / {@link WoodSellerWithCache}, this uses the intended
 * <em>guillotine</em> cut: every cut runs the full height or full width of the board, so a
 * rectangle is only ever split into two pieces. That property makes a simple {@code O(m*n*(m+n))}
 * bottom-up table exact and optimal.</p>
 *
 * @author mishe
 */
public class WoodSellerLeetCode {
	    
    /**
     * Computes the maximum revenue for an {@code m x n} board using bottom-up dynamic programming.
     *
     * <p>{@code dp[w][h]} holds the best price for a {@code w x h} sub-board. It is seeded with the
     * direct sale price of any listed piece of exactly that size, then improved by trying every
     * full-width horizontal cut ({@code dp[a][h] + dp[w-a][h]}) and every full-height vertical cut
     * ({@code dp[w][a] + dp[w][h-a]}). Iterating sizes in increasing order guarantees both halves
     * are already solved, so {@code dp[m][n]} is the answer.</p>
     *
     * @param m      board height
     * @param n      board width
     * @param prices available pieces, each as {@code [height, width, price]}
     * @return the maximum achievable revenue
     */
    public long sellingWood(int m, int n, int[][] prices) {
    	long[][] dp = new long[m + 1][n + 1];
        for (int[] p : prices)
            dp[p[0]][p[1]] = p[2];
        for (int w = 1; w <= m; ++w) {
            for (int h = 1; h <= n; ++h) {
                for (int a = 1; a <= w / 2; ++a)
                    dp[w][h] = Math.max(dp[w][h], dp[a][h] + dp[w - a][h]);
                for (int a = 1; a <= h / 2; ++a)
                    dp[w][h] = Math.max(dp[w][h], dp[w][a] + dp[w][h - a]);
            }
        }
        System.out.print("1. ");
        for (int w = 1; w <= m; w++) {
    		if (w > 1) System.out.println();
    		System.out.print(w + ". ");
        	for (int h = 1; h <= n; h++) {
        		if (h > 1) System.out.print(", ");
        		System.out.print(dp[w][h]);
        	}
        }
        System.out.println();
        return dp[m][n];
    }

	/**
	 * Ad-hoc test harness. The active assertion checks the 20x13 LeetCode example expects
	 * {@code 70}, which this guillotine-cut DP produces (and which the corner-cut variants do not).
	 */
	public static void main(String[] args) {
		WoodSellerLeetCode sol = new WoodSellerLeetCode();
		
//		int[][] prices1 = new int[][] { {1,4,2},{2,2,7},{2,1,3}};
//		PricedRectangle pr;
//		Rectangle rect;
//		List<Rectangle> rectList;
		
//		pr = new PricedRectangle(2, 1, 3);
//		rect = new Rectangle(2, 1);
//		rectList = sol.splitRectangle(rect, pr);
//		Assert.assertTrue(rectList.isEmpty());
//		System.out.println("splitRectangle(" + rect + ", " + pr + " PASSED");
//		
//		pr = new PricedRectangle(2, 1, 3);
//		rect = new Rectangle(3, 5);
//		rectList = sol.splitRectangle(rect, pr);
//		Assert.assertFalse(rectList.isEmpty());
//		Assert.assertEquals(3, rectList.size());
//		Assert.assertEquals("[Rect[1, 1], Rect[2, 4], Rect[1, 4]]", String.valueOf(rectList));
//		System.out.println("splitRectangle(" + rect + ", " + pr + " PASSED");
//
//		Assert.assertEquals(2, sol.sellingWood(1, 5, prices1));
//		Assert.assertEquals(10, sol.sellingWood(2, 3, prices1));
//		Assert.assertEquals(0, sol.sellingWood(1, 1, prices1));
//		Assert.assertEquals(17, sol.sellingWood(2, 5, prices1));
//		Assert.assertEquals(19, sol.sellingWood(3, 5, prices1));
//		System.out.println("PASSED");
//
//		int[][] prices2 = new int[][] { {3,2,10},{1, 4, 2},{4,1,3}};
//		Assert.assertEquals(32, sol.sellingWood(4, 6, prices2));
//		
		int[][] prices3 = {{5,10,15}, {7,12,24}, {15,12,1}, {17,3,10}, {20,9,22}, {5,13,15}, {16,7,28}, {12,10,29}, {2,9,1}, {14,6,15}, {20,8,20}};
		Assert.assertEquals(70, sol.sellingWood(20, 13, prices3));
		System.out.println("PASSED");
		
		// [[5,10,15],[7,12,24],[15,12,1],[17,3,10],[20,9,22],[5,13,15],[16,7,28],[12,10,29],[2,9,1],[14,6,15],[20,8,20]]
		
	
	}

}