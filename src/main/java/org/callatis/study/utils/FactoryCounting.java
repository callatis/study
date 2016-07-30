package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.List;

public class FactoryCounting {

	public FactoryCounting() {
		// empty
	}
	
	public long count(int n, int m, String[] county) {
		int k = county.length;
		for (String e : county) {
			assert(e.length() <= k);
		}
		return combinations(n, m, county, k).size();
	}
	
	public List<Pair<List<Integer>>> combinations(int n, int m, String[] county, int k) {
		assert(n > 0 && n < 9 && m > 0 && m < 9 && county.length > 0 && county.length < 31);
		List<Pair<List<Integer>>> result = new ArrayList<Pair<List<Integer>>>();
		if (n == 0) {
			List<List<Integer>> combKM = new Combinations().combNK(k, m);
			for (List<Integer> list : combKM) {
				Pair<List<Integer>> pair = new Pair<List<Integer>>();
				pair.x = new ArrayList<Integer>();
				pair.y = list;
				result.add(pair);
			}
		} else if (m == 0) {
			List<List<Integer>> combKN = new Combinations().combNK(k, n);
			for (List<Integer> list : combKN) {
				Pair<List<Integer>> pair = new Pair<List<Integer>>();
				pair.x = list;
				pair.y = new ArrayList<Integer>();
				result.add(pair);
			}
		} else { // n > 0 && m > 0
			if (n > 0) { // put X on last position - compute combinations (n - 1, m, k - 1)
				String xString = county[k - 1];
				List<Pair<List<Integer>>> combinations = combinations(n - 1, m, county, k - 1);
				for (Pair<List<Integer>> pair : combinations) {
					List<Integer> xList = pair.x;
					List<Integer> yList = pair.y;
					boolean canReachAllYs = true;
					for (Integer y : yList) {
						if ('Y' != xString.charAt(y)) {
							canReachAllYs = false;
							break;
						}
					}
					if (canReachAllYs) {
						xList.add(k - 1);
						result.add(pair);
					}
				}
			} 
			if (m > 0) { // put Y on last position - compute combinations (n, m - 1, k - 1)
				List<Pair<List<Integer>>> combinations = combinations(n, m - 1, county, k - 1);
				for (Pair<List<Integer>> pair : combinations) {
					List<Integer> xList = pair.x;
					List<Integer> yList = pair.y;
					String yString = county[k - 1];
					boolean canReachAllXs = true;
					for (Integer x : xList) {
						if ('Y' != yString.charAt(x)) {
							canReachAllXs = false;
							break;
						}
					}
					if (canReachAllXs) {
						yList.add(k - 1);
						result.add(pair);
					}
				}
			}
			
			if (k > (n + m)) {
				final List<Pair<List<Integer>>> combNoK = combinations(n, m, county, k - 1);
				result.addAll(combNoK);
			}
		}
		
		return result;
	}
	
	static class Pair<T> {
		public T x;
		public T y;
		
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

}
