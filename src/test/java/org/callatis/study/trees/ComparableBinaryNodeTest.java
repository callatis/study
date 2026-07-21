package org.callatis.study.trees;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.study.callatis.utils.IntComparator;

public class ComparableBinaryNodeTest {
	
	private final Comparator<Integer> comparator = new IntComparator();

	@Test
	public void test() {
		ComparableBinaryNode<Integer> node1 = new ComparableBinaryNode<>(7, this.comparator, 
				null, null);
		ComparableBinaryNode<Integer> node2 = new ComparableBinaryNode<>(9, this.comparator, null, null);
		assertEquals(-1, node1.compareTo(node2.getVal()));
	}

}
