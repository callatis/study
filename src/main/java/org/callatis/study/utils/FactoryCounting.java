package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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
	
	@Test
	public void test112() throws Exception {
		final String[] county = new String[] { "NY", "YN" };
		System.out.println("(1, 1, 2)=" + combinations(1, 1, county, 2));
		Assert.assertEquals(2, count(1, 1, county));
	}
	
	@Test
	public void test213() throws Exception {
		final String[] county = new String[] { "NYY", "YNY", "YYN" };
		System.out.println("(2, 1, 3)=" + combinations(2, 1, county, 3));
		Assert.assertEquals(3, count(2, 1, county));
	}
	
	@Test
	public void test114() throws Exception {
		final String[] county = new String[] { "NYYY", "YNYY", "YYNY", "YYYN" };
		System.out.println("(1, 1, 4)=" + combinations(1, 1, county, 4));
		Assert.assertEquals(12, count(1, 1, county));
	}
	
	@Test
	public void test125() throws Exception {
		final String[] county = new String[] { "NYYYY", "YNYYN", "YYNYY", "YYYNY", "YNYYN" };
		System.out.println("(1, 2, 5)=" + combinations(1, 2, county, 5));
		Assert.assertEquals(24, count(1, 2, county));
	}
	
	@Test
	public void test226() throws Exception {
		final String[] county = new String[] { "NYYYYN", "YNYYNY", "YYNYYY", "YYYNYN", "YNYYNY", "NYYNYN" };
		System.out.println("(2, 2, 6)=" + combinations(2, 2, county, 6));
		Assert.assertEquals(32, count(2, 2, county));
	}
	
	static class Pair<T> {
		public T x;
		public T y;
		
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

}
