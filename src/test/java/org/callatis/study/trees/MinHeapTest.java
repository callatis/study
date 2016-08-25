package org.callatis.study.trees;

import static org.junit.Assert.*;

import java.util.Comparator;

import org.callatis.study.trees.MinHeap;
import org.junit.Test;
import org.study.callatis.utils.IntComparator;

public class MinHeapTest {
	
	private Comparator<Integer> comparator = new IntComparator();

	@Test
	public void test1() {
		MinHeap<Integer> minHeap = new MinHeap<Integer>(this.comparator);
		minHeap.add(1);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(0, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertTrue(minHeap.isEmpty());
		assertEquals(null, minHeap.peek());
		assertNull(minHeap.getRoot());
	}

	@Test
	public void test2() {
		MinHeap<Integer> minHeap = new MinHeap<Integer>(this.comparator);
		minHeap.add(1).add(2);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertFalse(minHeap.isEmpty());
		assertEquals(2, minHeap.peek().intValue());
		assertEquals(0, minHeap.getRoot().getHeight());
	}

	@Test
	public void test3() {
		MinHeap<Integer> minHeap = new MinHeap<Integer>(this.comparator);
		minHeap.add(1).add(2).add(3);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertFalse(minHeap.isEmpty());
		assertEquals(2, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
	}

	@Test
	public void test3Reverse() {
		MinHeap<Integer> minHeap = new MinHeap<Integer>(this.comparator);
		minHeap.add(3).add(2).add(1);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertFalse(minHeap.isEmpty());
		assertEquals(2, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
	}

	@Test
	public void testComplex1() {
		MinHeap<Integer> minHeap = new MinHeap<Integer>(this.comparator);
		int[] vals = new int[] { 1, 2, 3, 4, 5, 6, 7 };
		for (int i = vals.length; i > 0; i--) {
			minHeap.add(vals[i - 1]);
			System.out.println("Added " + vals[i - 1]);
		}
		System.out.println("Created heap: " + minHeap.getRoot());
		assertEquals(1, minHeap.peek().intValue());
//		assertEquals(2, minHeap.getRoot().getHeight());
		
		for (int i = 0; i < vals.length; i++) {
			assertEquals(vals[i], minHeap.peek().intValue());
			int firstElem = minHeap.pop();
			System.out.println("Popped " + vals[i]);
			assertEquals(vals[i], firstElem);
			assertEquals(i == vals.length - 1 ? true : false, minHeap.isEmpty());
		}
	}

}
