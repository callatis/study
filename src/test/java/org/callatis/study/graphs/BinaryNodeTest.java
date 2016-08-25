package org.callatis.study.graphs;

import org.junit.Assert;

import org.callatis.study.trees.BinaryNode;
import org.junit.Test;

public class BinaryNodeTest {

	@Test
	public void testGetHeight0() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, null, null);
		Assert.assertEquals(0, root.getHeight());
		Assert.assertEquals(0, root.getNumDescendants());
		Assert.assertTrue(root.isFull());
		Assert.assertTrue(root.isComplete());
	}

	@Test
	public void testGetHeightLeft1() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				new BinaryNode<Integer>(88, null, null), 
				null);
		Assert.assertEquals(1, root.getHeight());
		Assert.assertEquals(1, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertTrue(root.isComplete());
	}

	@Test
	public void testGetHeightRight1() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				null, 
				new BinaryNode<Integer>(88, null, null));
		Assert.assertEquals(1, root.getHeight());
		Assert.assertEquals(1, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertFalse(root.isComplete());
	}

	@Test
	public void testGetHeightLeft2() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				new BinaryNode<Integer>(88, 
						new BinaryNode<Integer>(77, null, null), null), 
				null);
		Assert.assertEquals(2, root.getHeight());
		Assert.assertEquals(2, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertFalse(root.isComplete());
	}

	@Test
	public void testGetHeightRight2() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				null, new BinaryNode<Integer>(88, 
						null, 
						new BinaryNode<Integer>(77, null, null)));
		Assert.assertEquals(2, root.getHeight());
		Assert.assertEquals(2, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertFalse(root.isComplete());
	}

	@Test
	public void testGetHeightLeft1Right2() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				new BinaryNode<Integer>(66, null, null), 
				new BinaryNode<Integer>(88, 
						null, 
						new BinaryNode<Integer>(77, null, null)));
		Assert.assertEquals(2, root.getHeight());
		Assert.assertEquals(3, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertFalse(root.isComplete());
	}

	@Test
	public void testGetHeightLeft2Right1() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				new BinaryNode<Integer>(66, 
						new BinaryNode<Integer>(77, null, null), null), 
				new BinaryNode<Integer>(88, 
						null, 
						null));
		Assert.assertEquals(2, root.getHeight());
		Assert.assertEquals(3, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertTrue(root.isComplete());
	}

	@Test
	public void testGetHeightLeft2Right1Left1() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				new BinaryNode<Integer>(66, 
						new BinaryNode<Integer>(77, null, null), null), 
				new BinaryNode<Integer>(88, 
						new BinaryNode<Integer>(55, null, null), 
						null));
		Assert.assertEquals(2, root.getHeight());
		Assert.assertEquals(4, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertFalse(root.isComplete());
	}

	@Test
	public void testGetHeightLeft3Right1() {
		BinaryNode<Integer> root = new BinaryNode<Integer>(99, 
				new BinaryNode<Integer>(66, 
						new BinaryNode<Integer>(55, null, null), 
						new BinaryNode<Integer>(77, null, null)), 
				new BinaryNode<Integer>(88, 
						null, 
						null));
		Assert.assertEquals(2, root.getHeight());
		Assert.assertEquals(4, root.getNumDescendants());
		Assert.assertFalse(root.isFull());
		Assert.assertTrue(root.isComplete());
	}

}
