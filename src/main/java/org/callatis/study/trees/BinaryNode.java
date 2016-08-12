package org.callatis.study.trees;

public class BinaryNode<T> {
	
	private final T val;
	
	private final BinaryNode<T> left;
	
	private final BinaryNode<T> right;
	
	private int height = -1;
	
	private int numDescendants = -1; 
	
	private Boolean complete, full;

	public BinaryNode(T val, BinaryNode<T> left, BinaryNode<T> right) {
		super();
		this.val = val;
		this.left = left;
		this.right = right;
	}

	public T getVal() {
		return val;
	}

	public BinaryNode<T> getLeft() {
		return left;
	}

	public BinaryNode<T> getRight() {
		return right;
	}
	
	public int getHeight() {
		if (this.height < 0) {
			this.height = computeHeight();
		}
		return this.height;
	}
	
	private int computeHeight() {
		return 1 + Math.max(this.left == null ? -1 : this.left.computeHeight(), 
				this.right == null ? -1 : this.right.computeHeight());
	}
	
	public int getNumDescendants() {
		if (this.numDescendants < 0) {
			this.numDescendants = ((this.left == null) ? 0 : (this.left.getNumDescendants() + 1))
					+ ((this.right == null) ? 0 : (this.right.getNumDescendants() + 1));
		}
		return this.numDescendants;
	}
	
	public boolean isFull() {
		if (this.full == null) {
			this.full = internalIsFull();
		}
		
		return this.full;
	}
	
	public boolean internalIsFull() { 
		if (this.left == null) return this.right == null;
		if (this.right == null) return false;
		return (this.left.isFull() && this.right.isFull() && this.left.getHeight() == this.right.getHeight());
	}
	
	public boolean isComplete() {
		if (this.complete == null) {
			this.complete = internalIsComplete();
		}
		
		return this.complete;
	}
	
	public boolean internalIsComplete() {
		if (this.left == null) return this.right == null;
		if (this.right == null) return this.left.getHeight() == 0; // no right => left must be leaf
		if (!this.left.isComplete()
				|| !this.right.isComplete()
				|| this.left.getHeight() > this.right.getHeight() + 1) return false;
		if (this.left.isFull()) {
			if (this.right.isFull()) return 
					((this.left.getHeight() == this.right.getHeight())
					|| (this.left.getHeight() == this.right.getHeight() + 1));
			else // right is complete, but not full anyway
				return this.right.getHeight() == this.left.getHeight();
			
		} else return (this.right.isFull() && this.left.getHeight() == this.right.getHeight() + 1);
	}

	@Override
	public String toString() {
		return "{ val: " + String.valueOf(this.val) + ", left: " + String.valueOf(this.left)
				+ ", right: " + String.valueOf(this.right);
	
	}
	
}
