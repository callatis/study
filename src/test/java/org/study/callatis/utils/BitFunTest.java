package org.study.callatis.utils;

import static org.junit.Assert.*;

import org.callatis.study.utils.BitFun;
import org.junit.Test;

public class BitFunTest {

	@Test
	public void testLowestSetBit1() {
		assertEquals(1, BitFun.lowestSetBitValue(1));
		assertEquals(1, BitFun.countBits(1));
		assertEquals("[0]", String.valueOf(BitFun.toList(1)));
		assertEquals(1, BitFun.lowestSetBitValue(3));
		assertEquals(2, BitFun.countBits(3));
		assertEquals("[0, 1]", String.valueOf(BitFun.toList(3)));
		assertEquals(1, BitFun.lowestSetBitValue(5));
		assertEquals(2, BitFun.countBits(5));
		assertEquals("[0, 2]", String.valueOf(BitFun.toList(5)));
		assertEquals(1, BitFun.lowestSetBitValue(7));
		assertEquals(3, BitFun.countBits(7));
		assertEquals("[0, 1, 2]", String.valueOf(BitFun.toList(7)));
	}

	@Test
	public void testLowestSetBit2() {
		assertEquals(2, BitFun.lowestSetBitValue(2));
		assertEquals(1, BitFun.countBits(2));
		assertEquals("[1]", String.valueOf(BitFun.toList(2)));
		assertEquals(2, BitFun.lowestSetBitValue(6));
		assertEquals(2, BitFun.countBits(6));
		assertEquals("[1, 2]", String.valueOf(BitFun.toList(6)));
		assertEquals(2, BitFun.lowestSetBitValue(10));
		assertEquals(2, BitFun.countBits(10));
		assertEquals("[1, 3]", String.valueOf(BitFun.toList(10)));
		assertEquals(2, BitFun.lowestSetBitValue(14));
		assertEquals(3, BitFun.countBits(14));
		assertEquals("[1, 2, 3]", String.valueOf(BitFun.toList(14)));
	}

	@Test
	public void testLowestSetBit3() {
		assertEquals(4, BitFun.lowestSetBitValue(4));
		assertEquals(1, BitFun.countBits(4));
		assertEquals("[2]", String.valueOf(BitFun.toList(4)));
		assertEquals(4, BitFun.lowestSetBitValue(12));
		assertEquals(2, BitFun.countBits(12));
		assertEquals("[2, 3]", String.valueOf(BitFun.toList(12)));
		assertEquals(4, BitFun.lowestSetBitValue(20));
		assertEquals(2, BitFun.countBits(20));
		assertEquals("[2, 4]", String.valueOf(BitFun.toList(20)));
		assertEquals(4, BitFun.lowestSetBitValue(28));
		assertEquals(3, BitFun.countBits(28));
		assertEquals("[2, 3, 4]", String.valueOf(BitFun.toList(28)));
	}
	
	@Test
	public void testLowestSetBitPosition1() {
		assertEquals(0, BitFun.lowestSetBitPosition(1));
		assertEquals(0, BitFun.lowestSetBitPosition(3));
		assertEquals(0, BitFun.lowestSetBitPosition(5));
		assertEquals(0, BitFun.lowestSetBitPosition(7));
	}

	@Test
	public void testLowestSetBitPosition2() {
		assertEquals(1, BitFun.lowestSetBitPosition(2));
		assertEquals(1, BitFun.lowestSetBitPosition(6));
		assertEquals(1, BitFun.lowestSetBitPosition(10));
		assertEquals(1, BitFun.lowestSetBitPosition(14));
	}

	@Test
	public void testLowestSetBitPosition3() {
		assertEquals(2, BitFun.lowestSetBitPosition(4));
		assertEquals(2, BitFun.lowestSetBitPosition(12));
		assertEquals(2, BitFun.lowestSetBitPosition(20));
		assertEquals(2, BitFun.lowestSetBitPosition(28));
	}
	
}
