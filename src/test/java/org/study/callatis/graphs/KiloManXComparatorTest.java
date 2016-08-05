package org.study.callatis.graphs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.SortedSet;
import java.util.TreeSet;

import org.callatis.study.graphs.KiloManX.Node;
import org.callatis.study.graphs.KiloManX.NodeComparator;
import org.junit.Test;

public class KiloManXComparatorTest {
	
	private final Node o1, o2, o3, o4;
	private final NodeComparator comparator;
	
	public KiloManXComparatorTest() {
		this.o1 = new Node(1, 1);
		this.o2 = new Node(1, 2);
		this.o3 = new Node(1, 2);
		this.o4 = new Node(2, 1);
		comparator = new NodeComparator();
	}
	
	@Test
	public void testComparator() {
		assertTrue(this.comparator.compare(o1, o2) < 0);
		assertTrue(this.comparator.compare(o2, o3) == 0);
		assertEquals(o2, o3);
		assertTrue(this.comparator.compare(o3, o4) < 0);
	}
	
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
