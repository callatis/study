package org.study.callatis.utils;

import java.util.Comparator;

public class IntComparator implements Comparator<Integer> {

		@Override
		public int compare(Integer o1, Integer o2) {
			if (o1 == null) return o2 == null ? 0 : 1;
			if (o2 == null) return o1 == null ? 0 : -1;
			return (int) Math.signum(o1.intValue() - o2.intValue());
		}
				
}
