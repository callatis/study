package org.study.callatis.utils;

import java.util.ArrayList;
import java.util.List;

import org.callatis.study.utils.BitCombinations;
import org.junit.Assert;
import org.junit.Test;

public class BitCombinationsTest {
	
	private BitCombinations bitCombinations = new BitCombinations();

	@Test
	public void test00() {
		final List<Integer> combNK = this.bitCombinations.combNK(0, 0);
		Assert.assertEquals(1, combNK.size());
		Assert.assertEquals(Integer.valueOf(0), combNK.get(0));
	}

	@Test
	public void test10() {
		final List<Integer> combNK = this.bitCombinations.combNK(1, 0);
		Assert.assertEquals(1, combNK.size());
		Assert.assertEquals(Integer.valueOf(0), combNK.get(0));
	}

	@Test
	public void test11() {
		List<Integer> combination = new ArrayList<Integer>(1);
		combination.add(1 << 0);
		Assert.assertEquals(combination, this.bitCombinations.combNK(1, 1));
	}

	@Test
	public void test21() {
		List<Integer> list = new ArrayList<>(1);
		Integer combination1 = 1 << 1;
		list.add(combination1);
		Integer combination2 = 1 << 0;
		list.add(combination2);
		final List<Integer> combNK = this.bitCombinations.combNK(2, 1);
		System.out.println("Comb(2, 1) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test22() {
		List<Integer> list = new ArrayList<>(1);
		Integer combination = (1 << 1) | (1 << 0);
		list.add(combination);
		final List<Integer> combNK = this.bitCombinations.combNK(2, 2);
		System.out.println("Comb(2, 2) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test31() {
		List<Integer> list = new ArrayList<>(1);
		Integer combination1 = (1 << 2);
		list.add(combination1);
		Integer combination2 = (1 << 1);
		list.add(combination2);
		Integer combination3 = (1 << 0);
		list.add(combination3);
		final List<Integer> combNK = this.bitCombinations.combNK(3, 1);
		System.out.println("Comb(3, 1) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test32() {
		List<Integer> list = new ArrayList<>(1);
		Integer combination1 = (1 << 2) | (1 << 1);
		list.add(combination1);
		Integer combination2 = (1 << 2) | (1 << 0);
		list.add(combination2);
		Integer combination3 = (1 << 1) | (1 << 0);
		list.add(combination3);
		final List<Integer> combNK = this.bitCombinations.combNK(3, 2);
		System.out.println("Comb(3, 2) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test33() {
		List<Integer> list = new ArrayList<>(1);
		Integer combination = (1 << 0) | (1 << 1) | (1 << 2);
		list.add(combination);
		final List<Integer> combNK = this.bitCombinations.combNK(3, 3);
		System.out.println("Comb(3, 3) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test42() {
		List<Integer> list = new ArrayList<>(6);
		
		Integer combination1 = (1 << 2) | (1 << 3);
		list.add(combination1);
		Integer combination2 = (1 << 1) | (1 << 3);
		list.add(combination2);
		Integer combination3 = (1 << 0) | (1 << 3);
		list.add(combination3);
		
		Integer combination4 = (1 << 1) | (1 << 2);
		list.add(combination4);
		Integer combination5 = (1 << 0) | (1 << 2);
		list.add(combination5);
		Integer combination6 = (1 << 0) | (1 << 1);
		list.add(combination6);
		
		
		final List<Integer> combNK = this.bitCombinations.combNK(4, 2);
		System.out.println("Comb(4, 2) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

}
