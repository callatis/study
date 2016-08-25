package org.callatis.study.trees;

import java.util.Comparator;

public class ComparableBinaryNode<T> extends BinaryNode<T> implements Comparable<T> {
	
	private Comparator<T> comparator; 

	public ComparableBinaryNode(T val, Comparator<T> comparator, 
			ComparableBinaryNode<T> left, ComparableBinaryNode<T> right) {
		super(val, left, right);
		this.comparator = comparator;
	}

	@Override
	public int compareTo(T o) {
		return this.comparator.compare(this.getVal(), o);
	}
	
}
