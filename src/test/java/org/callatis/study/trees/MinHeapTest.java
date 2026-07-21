package org.callatis.study.trees;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.study.callatis.utils.IntComparator;

/**
 * Unit tests for {@link MinHeap}.
 *
 * <p>These tests verify the two core heap guarantees on a tree-backed min-heap:
 * <ul>
 *   <li>{@link MinHeap#peek()} always returns the current minimum, and
 *       {@link MinHeap#pop()} returns elements in ascending order; and</li>
 *   <li>the backing {@link BinaryNode} tree stays balanced, checked indirectly
 *       through {@link BinaryNode#getHeight()}.</li>
 * </ul>
 * All tests use an {@link IntComparator} so "minimum" means the smallest integer.</p>
 */
public class MinHeapTest {
	
	private final Comparator<Integer> comparator = new IntComparator();

	/**
	 * Single element: after one {@code add} the value is the min and the root is a
	 * leaf (height 0); after {@code pop} the heap is empty.
	 */
	@Test
	public void test1() {
		MinHeap<Integer> minHeap = new MinHeap<>(this.comparator);
		minHeap.add(1);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(0, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertTrue(minHeap.isEmpty());
		assertEquals(null, minHeap.peek());
		assertNull(minHeap.getRoot());
	}

	/**
	 * Two elements added in sorted order: min is 1, tree has height 1; after
	 * popping 1 the min becomes 2 and the tree collapses to a single leaf.
	 */
	@Test
	public void test2() {
		MinHeap<Integer> minHeap = new MinHeap<>(this.comparator);
		minHeap.add(1).add(2);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertFalse(minHeap.isEmpty());
		assertEquals(2, minHeap.peek().intValue());
		assertEquals(0, minHeap.getRoot().getHeight());
	}

	/**
	 * Three elements added in ascending order: min is 1 and, thanks to AVL
	 * re-balancing, the tree stays height 1; popping 1 leaves 2 as the new min.
	 */
	@Test
	public void test3() {
		MinHeap<Integer> minHeap = new MinHeap<>(this.comparator);
		minHeap.add(1).add(2).add(3);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertFalse(minHeap.isEmpty());
		assertEquals(2, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
	}

	/**
	 * Same three values as {@link #test3()} but inserted in descending order,
	 * confirming the ordering result is independent of insertion order: min is
	 * still 1, height still 1, and popping 1 yields 2 as the new min.
	 */
	@Test
	public void test3Reverse() {
		MinHeap<Integer> minHeap = new MinHeap<>(this.comparator);
		minHeap.add(3).add(2).add(1);
		assertEquals(1, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
		
		int oneElem = minHeap.pop();
		assertEquals(1, oneElem);
		assertFalse(minHeap.isEmpty());
		assertEquals(2, minHeap.peek().intValue());
		assertEquals(1, minHeap.getRoot().getHeight());
	}

	/**
	 * End-to-end sort check: seven values are inserted in reverse order, then
	 * repeatedly popped. Each pop must return the next-smallest value
	 * ({@code 1..7} ascending) and the heap must report empty only after the last
	 * element is removed.
	 */
	@Test
	public void testComplex1() {
		MinHeap<Integer> minHeap = new MinHeap<>(this.comparator);
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
