package org.callatis.study.graphs;

import java.util.Comparator;

import org.callatis.study.trees.BinaryNode;
import org.callatis.study.trees.BinarySearchNode;
import org.junit.Assert;
import org.junit.Test;
import org.study.callatis.utils.IntComparator;

public class BinarySearchNodeTest {

	private Comparator<Integer> comparator = new IntComparator();

	
	@Test
	public void testBadLeft() {
		try {
			new BinarySearchNode<Integer>(0, this.comparator, 
				new BinarySearchNode<Integer>(1, comparator, null, null), 
				null);
			Assert.fail("Expected exception");
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("1 <= 0"));
		}
	}

	@Test
	public void testBadRight() {
		try {
			new BinarySearchNode<Integer>(1, this.comparator,
					null, 
					new BinarySearchNode<Integer>(0, comparator, null, null));
			Assert.fail("Expected exception");
		} catch (IllegalArgumentException e) {
			Assert.assertTrue(e.getMessage(), e.getMessage().contains("0 >= 1"));
		}
	}

	@Test
	public void testLeft1() {
		BinarySearchNode<Integer> left;
		BinarySearchNode<Integer> bst = new BinarySearchNode<Integer>(1, this.comparator, 
			(left = new BinarySearchNode<Integer>(0, comparator, null, null)), 
			null);
		BinarySearchNode<Integer> leftRight = (BinarySearchNode<Integer>) left.getRight();
		Assert.assertTrue(bst == bst.rotateLeft());
		// rotate right
		Assert.assertTrue(left == bst.rotateRight());
		Assert.assertTrue(left.getRight() == bst);
		Assert.assertTrue(leftRight == bst.getLeft());
	}

	@Test
	public void testRight1() {
		BinarySearchNode<Integer> right;
		BinarySearchNode<Integer> bst = new BinarySearchNode<Integer>(0, this.comparator,
				null, 
				(right = new BinarySearchNode<Integer>(1, comparator, null, null)));
		BinarySearchNode<Integer> rightLeft = (BinarySearchNode<Integer>) right.getLeft();

		Assert.assertTrue(bst == bst.rotateRight());
		// rotate left
		Assert.assertTrue(right == bst.rotateLeft());
		Assert.assertTrue(right.getLeft() == bst);
		Assert.assertTrue(rightLeft == bst.getRight());
	}

	@Test
	public void testComplexRotation() {
		BinarySearchNode<Integer> left;
		BinarySearchNode<Integer> bst = new BinarySearchNode<Integer>(6, this.comparator, 
				(left = new BinarySearchNode<Integer>(4, comparator, 
					new BinarySearchNode<Integer>(2, comparator, 
							new BinarySearchNode<Integer>(1, comparator, null, null), 
							new BinarySearchNode<Integer>(3, comparator, null, null)), 
					new BinarySearchNode<Integer>(5, comparator, null, null))), 
			null);
		Assert.assertTrue(bst == bst.rotateLeft());
		// rotate right
		Assert.assertTrue(left == bst.rotateRight());
		Assert.assertTrue(left.getRight() == bst);
	}

	@Test
	public void testToAVL1() {
		BinarySearchNode<Integer> avlNode;
		BinarySearchNode<Integer> bst = new BinarySearchNode<Integer>(6, this.comparator, 
				(avlNode = new BinarySearchNode<Integer>(4, comparator, 
					new BinarySearchNode<Integer>(2, comparator, 
							new BinarySearchNode<Integer>(1, comparator, null, null), 
							new BinarySearchNode<Integer>(3, comparator, null, null)), 
					new BinarySearchNode<Integer>(5, comparator, null, null))), 
			null);
		Assert.assertTrue(avlNode == bst.toAVL());
	}

	@Test
	public void testLinearToAVL() {
		BinarySearchNode<Integer> avlNode;
		BinarySearchNode<Integer> bst = new BinarySearchNode<Integer>(6, this.comparator,
				new BinarySearchNode<Integer>(5, comparator, 
					(avlNode = new BinarySearchNode<Integer>(4, comparator,
						new BinarySearchNode<Integer>(3, comparator, 
								new BinarySearchNode<Integer>(2, comparator, 
										new BinarySearchNode<Integer>(1, comparator, null, null), 
										null), null), 
						null)),
				null), 
			null);
		BinaryNode<Integer> result = bst.toAVL();
		Assert.assertTrue(avlNode == result);
	}

}
