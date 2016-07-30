package org.callatis.study.utils;

public class BitFun {
	
	private BitFun() {}
	
	public static int lowestSetBit(int a) {
		return a & ~(a - 1);
	}

}
