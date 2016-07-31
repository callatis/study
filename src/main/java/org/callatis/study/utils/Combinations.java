package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.List;

public class Combinations {

	public Combinations() {
		// empty
	}
	
	public List<List<Integer>> combNK(int n, int k) {
		assert(n >= k);
		List<List<Integer>> result = new ArrayList<List<Integer>>(); 
		if (k == 0) {
			result.add(new ArrayList<Integer>());
			return result;
		}
		if (n == k) {
			List<Integer> list = new ArrayList<Integer>(n);
			for (int i = 0; i < n; i++) {
				list.add(i);
			}
			result.add(list);
			return result;
		}
		for (int i = n - 1; i >= k - 1; i--) {
			List<List<Integer>> listList = combNK(i, k - 1);
			for (List<Integer> sublist : listList) {
				sublist.add(i);
				result.add(sublist);
			}
			
		}
		
		return result;
	}

}
