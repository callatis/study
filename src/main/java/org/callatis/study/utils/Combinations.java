package org.callatis.study.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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
	
	@Test
	public void test00() {
		final List<List<Integer>> combNK = combNK(0, 0);
		Assert.assertEquals(1, combNK.size());
		Assert.assertTrue(combNK.get(0).isEmpty());
	}

	@Test
	public void test10() {
		final List<List<Integer>> combNK = combNK(1, 0);
		Assert.assertEquals(1, combNK.size());
		Assert.assertTrue(combNK.get(0).isEmpty());
	}

	@Test
	public void test11() {
		List<Integer> combination = new ArrayList<Integer>(1);
		combination.add(0);
		List<List<Integer>> list = new ArrayList<>(1);
		list.add(combination);
		Assert.assertEquals(list, combNK(1, 1));
	}

	@Test
	public void test21() {
		List<List<Integer>> list = new ArrayList<>(1);
		List<Integer> combination1 = new ArrayList<Integer>(1);
		combination1.add(1);
		list.add(combination1);
		List<Integer> combination2 = new ArrayList<Integer>(1);
		combination2.add(0);
		list.add(combination2);
		final List<List<Integer>> combNK = combNK(2, 1);
		System.out.println("Comb(2, 1) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test22() {
		List<List<Integer>> list = new ArrayList<>(1);
		List<Integer> combination = new ArrayList<Integer>(1);
		combination.add(0);
		combination.add(1);
		list.add(combination);
		final List<List<Integer>> combNK = combNK(2, 2);
		System.out.println("Comb(2, 2) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test31() {
		List<List<Integer>> list = new ArrayList<>(1);
		List<Integer> combination1 = new ArrayList<Integer>(1);
		combination1.add(2);
		list.add(combination1);
		List<Integer> combination2 = new ArrayList<Integer>(1);
		combination2.add(1);
		list.add(combination2);
		List<Integer> combination3 = new ArrayList<Integer>(1);
		combination3.add(0);
		list.add(combination3);
		final List<List<Integer>> combNK = combNK(3, 1);
		System.out.println("Comb(3, 1) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test32() {
		List<List<Integer>> list = new ArrayList<>(1);
		List<Integer> combination1 = new ArrayList<Integer>(1);
		combination1.add(1);
		combination1.add(2);
		list.add(combination1);
		List<Integer> combination2 = new ArrayList<Integer>(1);
		combination2.add(0);
		combination2.add(2);
		list.add(combination2);
		List<Integer> combination3 = new ArrayList<Integer>(1);
		combination3.add(0);
		combination3.add(1);
		list.add(combination3);
		final List<List<Integer>> combNK = combNK(3, 2);
		System.out.println("Comb(3, 2) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test33() {
		List<List<Integer>> list = new ArrayList<>(1);
		List<Integer> combination = new ArrayList<Integer>(1);
		combination.add(0);
		combination.add(1);
		combination.add(2);
		list.add(combination);
		final List<List<Integer>> combNK = combNK(3, 3);
		System.out.println("Comb(3, 3) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

	@Test
	public void test42() {
		List<List<Integer>> list = new ArrayList<>(6);
		
		List<Integer> combination1 = new ArrayList<Integer>(1);
		combination1.add(2);
		combination1.add(3);
		list.add(combination1);
		List<Integer> combination2 = new ArrayList<Integer>(1);
		combination2.add(1);
		combination2.add(3);
		list.add(combination2);
		List<Integer> combination3 = new ArrayList<Integer>(1);
		combination3.add(0);
		combination3.add(3);
		list.add(combination3);
		
		List<Integer> combination4 = new ArrayList<Integer>(1);
		combination4.add(1);
		combination4.add(2);
		list.add(combination4);
		List<Integer> combination5 = new ArrayList<Integer>(1);
		combination5.add(0);
		combination5.add(2);
		list.add(combination5);
		List<Integer> combination6 = new ArrayList<Integer>(1);
		combination6.add(0);
		combination6.add(1);
		list.add(combination6);
		
		
		final List<List<Integer>> combNK = combNK(4, 2);
		System.out.println("Comb(4, 2) = " + combNK);
		Assert.assertEquals(list, combNK);
	}

}
