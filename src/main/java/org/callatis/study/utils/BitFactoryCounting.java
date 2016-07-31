package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.List;

public class BitFactoryCounting {

	public BitFactoryCounting() {
		// empty
	}
	
	public long count(int n, int m, String[] county) {
		int k = county.length;
		for (String e : county) {
			assert(e.length() <= k);
		}
		return combinations(n, m, county, k).size();
	}
	
	public List<BitMaskPair> combinations(int n, int m, String[] county, int k) {
		assert(n > 0 && n < 9 && m > 0 && m < 9 && county.length > 0 && county.length < 31);
		List<BitMaskPair> result = new ArrayList<BitMaskPair>();
		if (n == 0) {
			List<Integer> combKM = new BitCombinations().combNK(k, m);
			for (Integer list : combKM) {
				BitMaskPair pair = new BitMaskPair();
				pair.x = 0;
				pair.y = list;
				result.add(pair);
			}
		} else if (m == 0) {
			List<Integer> combKN = new BitCombinations().combNK(k, n);
			for (Integer list : combKN) {
				BitMaskPair pair = new BitMaskPair();
				pair.x = list;
				pair.y = 0;
				result.add(pair);
			}
		} else { // n > 0 && m > 0
			if (n > 0) { // put X on last position - compute combinations (n - 1, m, k - 1)
				int xBitMask = toBitMask(k - 1, county[k - 1]);
				List<BitMaskPair> combinations = combinations(n - 1, m, county, k - 1);
				for (BitMaskPair pair : combinations) {
					Integer yList = pair.y;
					boolean canReachAllYs = (xBitMask & yList) == yList;
					if (canReachAllYs) {
						pair.x |= (1 << (k - 1));
						result.add(pair);
					}
				}
			} 
			if (m > 0) { // put Y on last position - compute combinations (n, m - 1, k - 1)
				List<BitMaskPair> combinations = combinations(n, m - 1, county, k - 1);
				for (BitMaskPair pair : combinations) {
					Integer xList = pair.x;
					int yBitMask = toBitMask(k - 1, county[k - 1]);
					boolean canReachAllXs = (xList & yBitMask) == xList;
					if (canReachAllXs) {
						pair.y |= (1 << (k - 1));
						result.add(pair);
					}
				}
			}
			
			if (k > (n + m)) {
				final List<BitMaskPair> combNoK = combinations(n, m, county, k - 1);
				result.addAll(combNoK);
			}
		}
		
		return result;
	}
	
	private int toBitMask(int k, String countyRow) {
		int bitMask = 0;
		for (int i = 0; i < k; i++) {
			if (countyRow.charAt(i) == 'Y') {
				bitMask |= 1 << i;
			}
		}
		
		return bitMask;
	}
	
	static class BitMaskPair extends Pair<Integer> {

		@Override
		public String toString() {
			return "(" + BitFun.toList(x) + ", " + BitFun.toList(y) + ")";
		}
		
	}
	
	static class Pair<T> {
		public T x;
		public T y;
		
		public String toString() {
			return "(" + x + ", " + y + ")";
		}
	}

}
