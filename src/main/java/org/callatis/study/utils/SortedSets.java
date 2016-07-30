package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SortedSets {

	public SortedSets() {
		// static only
	}
	
	public static List<Integer> union(List<Integer> a, List<Integer> b, boolean duplicates) {
		int i = 0, j = 0;
		List<Integer> r = new ArrayList<Integer>(a.size() + b.size());
		while (i < a.size() && j < b.size()) {
			if (a.get(i) < b.get(j)) {
				r.add(a.get(i++));
			} else if (a.get(i) > b.get(j)) {
				r.add(b.get(j++));
			} else {
				r.add(a.get(i++));
				if (duplicates) r.add(b.get(j++)); else j++;
			}
		}
		
		while (i < a.size()) {
			r.add(a.get(i++));
		}
		while (j < b.size()) {
			r.add(b.get(j++));
		}
		
		return r;
	}
	
	@Test
	public void testUnion() throws Exception {
		List<Integer> a = Arrays.<Integer>asList(new Integer[] {1, 3, 5, 7});
		List<Integer> b = Arrays.<Integer>asList(new Integer[] {2, 4, 5, 6});
		System.out.println(SortedSets.union(a, b, true));
		System.out.println(SortedSets.union(a, b, false));
	}

}
