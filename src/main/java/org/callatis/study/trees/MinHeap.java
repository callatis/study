/**
 * 
 */
package org.callatis.study.trees;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.callatis.study.utils.AuPair;

/**
 * @author mpoplacenel
 */
public class MinHeap<T> {
	
	private BinaryNode<T> root;
	
	private Comparator<T> comparator;
	
	public MinHeap(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	public MinHeap<T> add(final T val) {
		this.root = addNode(new BinaryNode<T>(val, null, null));
		return this;
	}

	public T pop() {
		if (this.root == null) return null;
		BinaryNode<T> leafParent = this.root;
		boolean left = false;
		BinaryNode<T> leaf = this.root;
		while (leaf.getRight() != null || leaf.getLeft() != null) {
			if (leaf.getRight() != null) {
				leafParent = leaf;
				leaf = leaf.getRight();
				left = false;
			} else {
				leafParent = leaf;
				leaf = leaf.getLeft();
				left = true;
			}
		}
		T result = this.root.getVal();
		
		if (leaf == this.root) { // only one element
			this.root = null;
			return result;
		}
		
		leaf.setLeft(leaf == this.root.getLeft() ? null : this.root.getLeft());
		leaf.setRight(leaf == this.root.getRight() ? null : this.root.getRight());
		this.root = leaf;
		if (left) {
			leafParent.setLeft(null);
		} else {
			leafParent.setRight(null);
		}
		bubbleRootDown();
		
		return result;
	}
	
	public T peek() {
		return this.root == null ? null : this.root.getVal();
	}
	
	public boolean isEmpty() {
		return this.root == null;
	}

	BinaryNode<T> getRoot() {
		return this.root;
	}

	Comparator<T> getComparator() {
		return this.comparator;
	}

	BinaryNode<T> addNode(final BinaryNode<T> node) {
		if (this.root == null) {
			return this.root = node;
		}
		List<BinaryNode<T>> path = new ArrayList<BinaryNode<T>>();
		BinaryNode<T> right = this.root;
		while (right.getRight() != null) {
			path.add(right);
			right = right.getRight();
		}
		path.add(right);
		if (right.getLeft() == null) {
			right.setLeft(node);
		} else {
			right.setRight(node);
		}
		path.add(node);
		
		bubbleUp(path);
		
		return this.root.toAVL();
	}
	
	protected void swap(BinaryNode<T> parent, BinaryNode<T> child) {
		BinaryNode<T> left = parent.getLeft();
		BinaryNode<T> right = parent.getRight();
		parent.setLeft(parent == child.getLeft() ? child : child.getLeft());
		parent.setRight(parent == child.getRight() ? child : child.getRight());
		child.setLeft(child == left ? parent : left);
		child.setRight(child == right ? parent : right);
	}

	protected void bubbleUp(List<BinaryNode<T>> path) {
		BinaryNode<T> node = path.get(path.size() - 1);
		for (int i = path.size() - 2; i >= 0; i--) {
			BinaryNode<T> parent = path.get(i);
			if (this.comparator.compare(node.getVal(), parent.getVal()) < 0) {
				swap(parent, node);
				if (i == 0) {
					this.root = node;
				} else {
					BinaryNode<T> grannie = path.get(i - 1);
					if (grannie.getLeft() == parent) {
						grannie.setLeft(node);
					} else if (grannie.getRight() == parent) {
						grannie.setRight(node);
					} else {
						System.out.println("Swapped " + parent + " with " + node 
								+ ", but parent matches neither grannie.left = " + grannie.getLeft()
								+ " nor grannie.right = " + grannie.getRight());
					}
				}
			} else {
				node = parent;
			}
		}
	}

	protected void bubbleRootDown() {
		BinaryNode<T> node = this.root;
		while (node != null) {
			if ((node.getLeft() == null 
						|| this.comparator.compare(node.getVal(), node.getLeft().getVal()) <= 0)
					&& (node.getRight() == null || 
						this.comparator.compare(node.getVal(), node.getRight().getVal()) < 0)) { 
				// > both kids are good - we're done
				return;
			}
			if (node.getLeft() == null && node.getRight() == null) {
				// leaf node - we're done
				return;
			}
			
			// take smaller of left and right
			AuPair<Boolean, BinaryNode<T>> minPair = minNonNull(node.getLeft(), node.getRight());
			// check if larger than min, and if yes swap
			if (minPair.x) { // node.left < node.right 
				BinaryNode<T> parent = checkAndSwap(node, node.getLeft());
				if (parent == node) { // no swap, exit
					node = null;
				} else if (node == this.root) {
					node = parent.getLeft();
					this.root = parent;
				}
			} else {
				BinaryNode<T> parent = checkAndSwap(node, node.getRight());
				if (parent == node) { // no swap, exit
					node = null;
				} else if (node == this.root) {
					node = parent.getRight();
					this.root = parent;
				}
			}
		}
	}
	
	/**
	 * Return min(node1, node2)
	 * @param node1
	 * @param node2
	 * @return [node1 < node2, min(node1, node2)], where null is the highest
	 */
	protected AuPair<Boolean, BinaryNode<T>> minNonNull(BinaryNode<T> node1, BinaryNode<T> node2) {
		if (node1 == null) return new AuPair<Boolean, BinaryNode<T>>(Boolean.FALSE, node2);
		if (node2 == null) return new AuPair<Boolean, BinaryNode<T>>(Boolean.TRUE, node1);
		return this.comparator.compare(node1.getVal(), node2.getVal()) < 0 
				? new AuPair<Boolean, BinaryNode<T>>(Boolean.TRUE, node1) 
				: new AuPair<Boolean, BinaryNode<T>>(Boolean.FALSE, node2);
	}
	
	/**
	 * Checks if child > parent, and if yes, swaps them. 
	 * 
	 * @param parent parent
	 * @param child child
	 * 
	 * @return the parent after the swap - same if child <= parent, node2 otherwise.
	 */
	protected BinaryNode<T> checkAndSwap(BinaryNode<T> parent, BinaryNode<T> child) {
		if (parent == null) {
			return null;
		}
		
		if (child != null && this.comparator.compare(parent.getVal(), child.getVal()) > 0) { // node1 > node2
			swap(parent, child);
			return child;
		}
		
		return parent;
	}

}
