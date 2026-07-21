package org.callatis.study.graphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.SortedSet;
import java.util.TreeSet;

import org.callatis.study.graphs.KiloManX.Node;
import org.callatis.study.graphs.KiloManX.NodeComparator;
import org.junit.Test;

/**
 * Tests for {@link NodeComparator}, the ordering used by the Dijkstra priority queue in
 * {@link KiloManX}.
 *
 * <p>
 * The comparator sorts nodes by ascending shots first and by ascending weapons bitmask as a
 * tie-breaker. These tests verify both the pairwise ordering and that the tie-breaker keeps
 * distinct-but-equal-shot nodes as separate elements inside a {@link TreeSet}.
 * </p>
 */
public class KiloManXComparatorTest {
	
	private final Node o1, o2, o3, o4;
	private final NodeComparator comparator;
	
	/**
	 * Builds the shared fixtures: {@code o1=(shots=1,weapons=1)}, {@code o2=(1,2)},
	 * {@code o3=(1,2)} (equal to {@code o2}), and {@code o4=(2,1)}.
	 */
	public KiloManXComparatorTest() {
		this.o1 = new Node(1, 1);
		this.o2 = new Node(1, 2);
		this.o3 = new Node(1, 2);
		this.o4 = new Node(2, 1);
		comparator = new NodeComparator();
	}
	
	/**
	 * Verifies pairwise ordering: equal shots order by weapons ({@code o1 < o2}), identical nodes
	 * compare equal ({@code o2 == o3}), and fewer shots order first ({@code o3 < o4}).
	 */
	@Test
	public void testComparator() {
		assertTrue(this.comparator.compare(o1, o2) < 0);
		assertTrue(this.comparator.compare(o2, o3) == 0);
		assertEquals(o2, o3);
		assertTrue(this.comparator.compare(o3, o4) < 0);
	}
	
	/**
	 * Verifies that a {@link TreeSet} using the comparator deduplicates the equal pair
	 * ({@code o2}/{@code o3}) while keeping the other nodes, and iterates in ascending order.
	 */
	@Test
	public void testTreeSet() {
		SortedSet<Node> nodeSet = new TreeSet<Node>(comparator);
		nodeSet.add(this.o4);
		nodeSet.add(this.o3);
		nodeSet.add(this.o2);
		nodeSet.add(this.o1);
		assertEquals(String.valueOf(nodeSet), 3, nodeSet.size());
		assertEquals(o1, nodeSet.first());
		nodeSet.remove(o1);
		assertEquals(o3, nodeSet.first());
		nodeSet.remove(o2);
		assertEquals(o4, nodeSet.first());
	}

}
