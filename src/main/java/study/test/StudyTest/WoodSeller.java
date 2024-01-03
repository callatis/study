package study.test.StudyTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import junit.framework.Assert;

/**
 * Solution for {@link https://leetcode.com/problems/selling-pieces-of-wood/description/}.
 * 
 * Check out {@link WoodSellerWithCache} for the final version of this class. 
 * 
 * @author mishe
 */
public class WoodSeller {
	    
    public long sellingWood(int m, int n, int[][] prices) {
        SortedMap<Integer, List<PricedRectangle>> prMap = Collections.unmodifiableSortedMap(toPRMap(prices));
        System.out.println("Composed PR Map: " + prMap);
        List<PricedRectangle> prList = cutRectangle(new Rectangle(m, n), prMap, 0);
        int cost = 0;
        if (prList != null) {
	        for (PricedRectangle pr: prList) {
	        	cost += pr.price;
	        }
        }
        return Long.valueOf(cost);
    }

    private List<PricedRectangle> cutRectangle(Rectangle rect, SortedMap<Integer, List<PricedRectangle>> prMap, int level) {
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
	                    List<PricedRectangle> subSolutions = cutRectangle(cutRect, prMap, level + 1);
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
		WoodSeller sol = new WoodSeller();
		
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
		
		// [[5,10,15],[7,12,24],[15,12,1],[17,3,10],[20,9,22],[5,13,15],[16,7,28],[12,10,29],[2,9,1],[14,6,15],[20,8,20]]
		
	
	}

	//    public int backupCode() {
//  SortedMap<Integer, List<Rectangle>> workMap = new TreeMap<Integer, List<Rectangle>>();
//  addToMap(workMap, new Rectangle(m, n));
//  boolean doContinue = true;
//  while (doContinue) {
//      // remove all prices with area larger than larger area in the map
//      // and with h or w larger than the largest in the map.
//      // Alternately, we can just ignore them.
//      Iterator workMapIter = workMap.entries().iterator();
//      while (workIter.hasNext()) { // for each rectangle
//          Rectangle rect = work.next();
//          Iterator prIter = prMap.entries().iterator();
//          while (prIter.hasNext()) { // for each priced piece
//              Map.Entry<Integer, List<PricedRectangle>> mapE = prIter.next();
//              PricedRectangle pr = mapE.value();
//              if (pr.h * pr.w <= rect.h * rect.w) { // current priced rect fits
//                  // cut the priced piece and add the two remaining pieces to the sorted work map
//                  List<Rectangle> cutRects = cutRectangle(rect, pr);
//                  for (Rectangle cutRect : cutRects) {
//                      addToMap(workMap, cutRect);
//                  }
//                  workMap.remove(rect);
//              }
//          }
//      }
//  }
//  return 0; 
//}
//

//  private class PRComp implements Comparator<PricedRectangle> {
//
//      @Override
//      public int compare(PricedRectangle pr1, PricedRectangle pr2) {
//          if (pr1 == null) {
//              return pr2 == null ? 0 : -1;
//          } else if (pr2 == null) {
//              return 1;
//          }
//          // both pr1 and pr2 are not null
//          return Integer.compare(pr1.price, pr2.price);
//      }
//  }
//


}