package org.study.callatis.utils;

import static org.junit.Assert.*;

import org.callatis.study.utils.BitFun;
import org.junit.Test;

public class BitFunTest {

	@Test
	public void testLowestSetBit1() {
		assertEquals(1, BitFun.lowestSetBit(1));
		assertEquals(1, BitFun.lowestSetBit(3));
		assertEquals(1, BitFun.lowestSetBit(5));
		assertEquals(1, BitFun.lowestSetBit(7));
	}

	@Test
	public void testLowestSetBit2() {
		assertEquals(2, BitFun.lowestSetBit(2));
		assertEquals(2, BitFun.lowestSetBit(6));
		assertEquals(2, BitFun.lowestSetBit(10));
		assertEquals(2, BitFun.lowestSetBit(14));
	}

	@Test
	public void testLowestSetBit3() {
		assertEquals(4, BitFun.lowestSetBit(4));
		assertEquals(4, BitFun.lowestSetBit(12));
		assertEquals(4, BitFun.lowestSetBit(20));
		assertEquals(4, BitFun.lowestSetBit(28));
	}

}
