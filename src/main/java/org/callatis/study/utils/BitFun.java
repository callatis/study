package org.callatis.study.utils;

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

}
