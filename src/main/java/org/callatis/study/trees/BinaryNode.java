package org.callatis.study.trees;

public class BinaryNode<T> {
	
	private final T val;
	
	private BinaryNode<T> left;
	
	private BinaryNode<T> right;
	
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
	
	public void setLeft(BinaryNode<T> left) {
		this.left = left;
		resetCalculations();
	}

	public void setRight(BinaryNode<T> right) {
		this.right = right;
		resetCalculations();
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
	
	/**
	 * A "full" tree is what certain books call "perfect", i.e. it has 2^k - 1 nodes and 
	 * at all levels, height of left equals height of right. 
	 * 
	 * @see #internalIsFull()
	 * 
	 * @return take a guess ;) 
	 */
	public boolean isFull() {
		if (this.full == null) {
			this.full = internalIsFull();
		}
		
		return this.full;
	}
	
	/**
	 * A complete tree is one that has the min # of levels, 
	 * and nodes on last level are as left as possible.
	 * 
	 * @see #internalIsComplete()
	 *  
	 * @return take a guess ;) 
	 */
	public boolean isComplete() {
		if (this.complete == null) {
			this.complete = internalIsComplete();
		}
		
		return this.complete;
	}
	
	@Override
	public String toString() {
		return "{val: " + String.valueOf(this.val) + ", left: " + String.valueOf(this.left)
				+ ", right: " + String.valueOf(this.right) + "}";
	
	}
	
	public BinaryNode<T> rotateRight() {
		if (this.left == null) {
			return this;
		}
		BinaryNode<T> newRoot = getLeft();
		setLeft(getLeft().getRight());
		newRoot.setRight(this);
		
		return newRoot;
	}
	
	public BinaryNode<T> rotateLeft() {
		if (this.right == null) {
			return this;
		}
		BinaryNode<T> newRoot = getRight();
		setRight(getRight().getLeft());
		newRoot.setLeft(this);
		
		return newRoot;
	}
	
	/**
	 * Re-balances the tree so that it becomes AVL.
	 * 
	 * @return the root after re-balancing
	 */
	public BinaryNode<T> toAVL() {
		BinaryNode<T> result = this;
		while (true) {
			if (result.getLeft() == null && result.getRight() == null) { // leaves are AVL
				return result;
			}
			int leftHeight = result.getLeft() == null ? -1 : result.getLeft().getHeight();
			int rightHeight = result.getRight() == null ? -1 : result.getRight().getHeight();
			int delta = leftHeight - rightHeight;
			if (delta < -1) {
				result = result.rotateLeft();
			} else if (delta > 1) {
				result = result.rotateRight();
			} else { // we are AVL-compliant
				return result;
			}
		}
	}
	
	//////////////////////////////
	// PRIVATE AREA - DO NOT ENTER
	//////////////////////////////
	private void resetCalculations() {
		this.complete = this.full = null;
		this.numDescendants = -1;
		this.height = -1;
	}

	private boolean internalIsFull() { 
		if (this.left == null) return this.right == null;
		if (this.right == null) return false;
		return (this.left.isFull() && this.right.isFull() && this.left.getHeight() == this.right.getHeight());
	}
	
	private boolean internalIsComplete() {
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

}
