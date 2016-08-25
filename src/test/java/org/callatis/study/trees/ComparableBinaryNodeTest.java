package org.callatis.study.trees;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.callatis.study.trees.ComparableBinaryNode;
import org.junit.Test;
import org.study.callatis.utils.IntComparator;

public class ComparableBinaryNodeTest {
	
	private Comparator<Integer> comparator = new IntComparator();

	@Test
	public void test() {
		ComparableBinaryNode<Integer> node1 = new ComparableBinaryNode<Integer>(7, this.comparator, 
				null, null);
		ComparableBinaryNode<Integer> node2 = new ComparableBinaryNode<Integer>(9, this.comparator, null, null);
		assertEquals(-1, node1.compareTo(node2.getVal()));
	}

}
