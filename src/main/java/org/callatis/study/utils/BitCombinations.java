package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.List;

public class BitCombinations {

	public BitCombinations() {
		// empty
	}
	
	public List<Integer> combNK(int n, int k) {
		assert(n >= k);
		List<Integer> result = new ArrayList<Integer>(); 
		if (k == 0) {
			result.add(0);
			return result;
		}
		if (n == k) {
			Integer list = 0;
			for (int i = 0; i < n; i++) {
				list |= 1 << i;
			}
			result.add(list);
			return result;
		}
		for (int i = n - 1; i >= k - 1; i--) {
			List<Integer> listList = combNK(i, k - 1);
			for (Integer sublist : listList) {
				sublist|= 1 << i;
				result.add(sublist);
			}
			
		}
		
		return result;
	}
}
