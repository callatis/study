package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.List;

public class BitFun {
	
	private BitFun() {}
	
	public static int lowestSetBitValue(int a) {
		return a & ~(a - 1);
	}
	
	public static int lowestSetBitPosition(int a) {
		for (int i = 0; i < 64; i++) {
			int bit = 1 << i;
			if ((bit & a) > 0) {
				return i;
			}
		}
		
		return 0;
	}
	
	public static int countBits(int a) {
		int aa = a, count = 0;
		do {
			int firstBitVal = BitFun.lowestSetBitValue(aa);
			aa = aa & (~firstBitVal);
			count++;
		} while (aa > 0);
		
		return count;
	}
	
	public static List<Integer> toList(int a) {
		List<Integer> result = new ArrayList<Integer>();
		int aa = a;
		do {
			int firstBitPos = BitFun.lowestSetBitPosition(aa);
			int firstBitVal = BitFun.lowestSetBitValue(aa);
			aa = aa & (~firstBitVal);
			result.add(firstBitPos);
		} while (aa > 0);
		
		return result;
	}

}
