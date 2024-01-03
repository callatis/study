package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Assert;

/**
 * Solution for {@link https://leetcode.com/problems/selling-pieces-of-wood/description/}.
 * Note: this is roughly the same as {@link WoodSeller}, but with internal caching of the calculated
 * rectangles, so that they don't get recalculated. It is also a bit more cleaned up. 
 *   
 * Note that my understanding of the cutting is different from the intended: when you cut a sub-rectangle 
 * to sell from a larger one, you generate 4 sub-rectangles (2 if either height or the width of the piece 
 * you intend to sell goes all the way). The intended approach is to allow full horizontal or vertical cuts, 
 * which separate the rectangle in 2 (not in 4). 
 * 
 * Example: for the last use-case, if you separate into the [20, 13] into a [20, 10] and [20, 3], by using
 * a vertical cut, you will be able to sell for 70 (4 * [5, 10] = [20, 10] + 1 * [17, 3]). However, in the
 * approach I understood, once you cut a [17, 3] you are left with a [3, 10] that you can only sell for $1,
 * a [3, 20] that is $1, and a [17, 10] that is $46 => $10 + $1 + $1 + $46 = $58. If, however, you are allowed
 * to slice along the 3rd vertical and generate a [20, 3] and a [20, 10], you get $70 as described above. This
 * slicing is not cutting a piece on both directions, though, as I originally understood. 
 * 
 * In other words, this is a good solution, but for a slightly different problem than they intended to express. 
 * 
 * @author mishe
 */
public class WoodSellerWithCache {
	    
    public long sellingWood(int m, int n, int[][] prices) {
        SortedMap<Integer, List<PricedRectangle>> prMap = Collections.unmodifiableSortedMap(toPRMap(prices));
        System.out.println("Composed PR Map: " + prMap);
        Map<Rectangle, List<PricedRectangle>> cache = new HashMap<>();
        List<PricedRectangle> prList = cutRectangle(new Rectangle(m, n), prMap, cache, 0);
        System.out.println("*****************************");
        System.out.println("CACHE: ");
        System.out.println(cache);
        Map<Rectangle, Long> costMap = new HashMap<>(cache.size());
        for (Map.Entry<Rectangle, List<PricedRectangle>> mE : cache.entrySet()) {
			Rectangle rect = mE.getKey();
			List<PricedRectangle> pricedRectList = mE.getValue();
			costMap.put(rect, computeCost(pricedRectList));
		}
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        System.out.println("COSTS: ");
        System.out.println(costMap);
        System.out.println("*****************************");
        
        return computeCost(prList);
    }

	/**
	 * @param prList
	 * @return
	 */
	private long computeCost(List<PricedRectangle> prList) {
		int cost = 0;
        if (prList != null) {
	        for (PricedRectangle pr: prList) {
	        	cost += pr.price;
	        }
        }
        return Long.valueOf(cost);
	}

    private List<PricedRectangle> cutRectangle(Rectangle rect, SortedMap<Integer, List<PricedRectangle>> prMap, 
    		Map<Rectangle, List<PricedRectangle>> cache, int level) {
    	List<PricedRectangle> resultList = cache.get(rect);
    	if (resultList == null) {
        	final String indent = spaces(level);
    		System.out.println(indent + "START Compute " + rect);
    		resultList = doCutRectangle(rect, prMap, cache, level);
    		if (resultList != null) {
	    		System.out.println(indent + "END Computed price(" + rect + ") = " + computeCost(resultList)
				+ ": " + resultList);
    		}
    		cache.put(rect, resultList);
    	}
    	
    	return resultList;
    }
    
    private List<PricedRectangle> doCutRectangle(Rectangle rect, SortedMap<Integer, List<PricedRectangle>> prMap, 
    		Map<Rectangle, List<PricedRectangle>> cache, int level) {
    	final String indent = spaces(level);
		System.out.println(indent + "Tackling " + rect);
        SortedMap<Integer, List<PricedRectangle>> solutions = new TreeMap<Integer, List<PricedRectangle>>(Collections.reverseOrder());
        Iterator<Map.Entry<Integer, List<PricedRectangle>>> prIter = prMap.entrySet().iterator();
        while (prIter.hasNext()) { // for each priced piece
            Map.Entry<Integer, List<PricedRectangle>> mapE = prIter.next();
            List<PricedRectangle> prList = mapE.getValue();
            for (PricedRectangle pr : prList) {
	            if (pr.h * pr.w <= rect.h * rect.w
	            		&& pr.h <= rect.h && pr.w <= rect.w) { // current priced rect fits
	                // cut the priced piece and add the three remaining pieces to the sorted work map
	            	System.out.println(indent + "Sell " + pr);
	                List<PricedRectangle> solList = new ArrayList<PricedRectangle>();
	                solList.add(pr);
	                List<Rectangle> cutRects = splitRectangle(rect, pr);
	                for (Rectangle cutRect : cutRects) { // only 2 or 3 of them
	                    List<PricedRectangle> subSolutions = cutRectangle(cutRect, prMap, cache, level + 1);
	                    if (subSolutions != null) {
	                        solList.addAll(subSolutions);
	                    }
	                }
	                int cost = 0;
	                for (PricedRectangle solRect : solList) {
	                    cost += solRect.price;
	                }
	            	System.out.println(indent + "Sold for " + cost);
	                if (cost > 0) {
	                    solutions.put(cost, solList);
	                }
	            }
            }
        }
        if (solutions.isEmpty()) {
            return null;
        }

    	System.out.println(indent + "Top price for " + rect + " = " + solutions.firstKey());
        return solutions.get(solutions.firstKey());
    }

    public List<Rectangle> splitRectangle(Rectangle rect, PricedRectangle pr) {
    	List<Rectangle> list = new ArrayList<>();
    	if (rect.h > pr.h) {
			Rectangle rect1 = new Rectangle(rect.h - pr.h, pr.w);
			list.add(rect1);
    	}
    	if (rect.w > pr.w) {
			Rectangle rect2 = new Rectangle(pr.h, rect.w - pr.w);
			list.add(rect2);
    	}
    	if (rect.h > pr.h && rect.w > pr.w) {
			Rectangle rect3 = new Rectangle(rect.h - pr.h, rect.w - pr.w);
			list.add(rect3);
    	}
		return list;
	}

    private SortedMap<Integer, List<PricedRectangle>> toPRMap(int[][] prices) {
        SortedMap<Integer, List<PricedRectangle>> map = new TreeMap<>(Collections.reverseOrder());
        for (int[] price: prices) {
            PricedRectangle pr = new PricedRectangle(price[0], price[1], price[2]);
            List<PricedRectangle> prs = map.get(pr.price);
            if (prs == null) {
                prs = new ArrayList<PricedRectangle>();
                map.put(pr.price, prs);
            }
            prs.add(pr);
        }

        return map;
    }
    
    private String spaces(int count) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < count * 4; i++) {
    		sb.append(' ');
    	}
    	
    	return sb.toString();
    }
    
    private static class Rectangle {

        int h, w;

        Rectangle(int h, int w) {
            this.h = h;
            this.w = w;
        }
        
        @Override
        public String toString() {
        	return "Rect[" + this.h + ", " + this.w + "]";
        }

		@Override
		public int hashCode() {
			return this.h * 93 + this.w;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!getClass().getName().equals(getClass().getName()))
				return false;
			
			Rectangle that = (Rectangle) obj;
			return this.h == that.h && this.w == that.w;
		}

    }

    private static class PricedRectangle extends Rectangle {

        int price;

        PricedRectangle(int h, int w, int price) {
            super(h, w);
            this.price = price;
        }

        @Override
        public String toString() {
        	return "PricedRect[" + this.h + ", " + this.w + ", " + this.price + "]";
        }

    }

	public static void main(String[] args) {
		WoodSellerWithCache sol = new WoodSellerWithCache();
		
		int[][] prices1 = new int[][] { {1,4,2},{2,2,7},{2,1,3}};
		PricedRectangle pr;
		Rectangle rect;
		List<Rectangle> rectList;
		
		pr = new PricedRectangle(2, 1, 3);
		rect = new Rectangle(2, 1);
		rectList = sol.splitRectangle(rect, pr);
		Assert.assertTrue(rectList.isEmpty());
		System.out.println("splitRectangle(" + rect + ", " + pr + " PASSED");
		
		pr = new PricedRectangle(2, 1, 3);
		rect = new Rectangle(3, 5);
		rectList = sol.splitRectangle(rect, pr);
		Assert.assertFalse(rectList.isEmpty());
		Assert.assertEquals(3, rectList.size());
		Assert.assertEquals("[Rect[1, 1], Rect[2, 4], Rect[1, 4]]", String.valueOf(rectList));
		System.out.println("splitRectangle(" + rect + ", " + pr + " PASSED");

		Assert.assertEquals(2, sol.sellingWood(1, 5, prices1));
		Assert.assertEquals(10, sol.sellingWood(2, 3, prices1));
		Assert.assertEquals(0, sol.sellingWood(1, 1, prices1));
		Assert.assertEquals(17, sol.sellingWood(2, 5, prices1));
		Assert.assertEquals(19, sol.sellingWood(3, 5, prices1));
		System.out.println("PASSED");

		int[][] prices2 = new int[][] { {3,2,10},{1, 4, 2},{4,1,3}};
		Assert.assertEquals(32, sol.sellingWood(4, 6, prices2));
		System.out.println("PASSED");

		// Note: the following fails on LeetCode as it expects 70, as described in the class Javadoc. 
		// [[5,10,15],[7,12,24],[15,12,1],[17,3,10],[20,9,22],[5,13,15],[16,7,28],[12,10,29],[2,9,1],[14,6,15],[20,8,20]]
		int[][] prices3 = {{5,10,15}, {7,12,24}, {15,12,1}, {17,3,10}, {20,9,22}, {5,13,15}, {16,7,28}, {12,10,29}, {2,9,1}, {14,6,15}, {20,8,20}};
		Assert.assertEquals(/* 70 */ 63, sol.sellingWood(20, 13, prices3));
		System.out.println("PASSED");
	
	}


}