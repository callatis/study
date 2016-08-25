package org.callatis.study.trees;

import java.util.Comparator;

public class BinarySearchNode<T> extends ComparableBinaryNode<T> {

	public BinarySearchNode(T val, Comparator<T> comparator, 
			ComparableBinaryNode<T> left,
			ComparableBinaryNode<T> right) {
		super(val, comparator, left, right);
		assertBST();
	}
	
	public void assertBST() {
		if (getLeft() != null && ((BinarySearchNode<T>) this.getLeft()).compareTo(getVal()) > 0) {
			throw new IllegalArgumentException("Must have left.val = " 
					+ getLeft().getVal() + " <= " + getVal() + " = this.val");
		}
		if (getRight() != null && ((BinarySearchNode<T>) getRight()).compareTo(this.getVal()) < 0) {
			throw new IllegalArgumentException("Must have right.val = " + getRight().getVal() 
					+ " >= " + getVal() + " = this.val");
		}

	}

	@Override
	public BinaryNode<T> rotateRight() {
		return assertBSTPreserved((BinarySearchNode<T>) super.rotateRight());
	}

	@Override
	public BinaryNode<T> rotateLeft() {
		return assertBSTPreserved((BinarySearchNode<T>) super.rotateLeft());
	}

	private BinarySearchNode<T> assertBSTPreserved(BinarySearchNode<T> result) {
		assertBST();
		result.assertBST();
		
		return result;
	}

}
