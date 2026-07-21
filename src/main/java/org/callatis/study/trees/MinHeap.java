/**
 * 
 */
package org.callatis.study.trees;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.callatis.study.utils.AuPair;

/**
 * A <b>binary min-heap</b> backed by an explicit, pointer-based binary tree
 * ({@link BinaryNode}) rather than the more common array/{@code ArrayList}
 * representation.
 *
 * <h2>Heap invariant</h2>
 * Every node's value is less than or equal to the values of its children
 * (ordering is defined by the supplied {@link Comparator}). Consequently the
 * smallest element is always at the {@link #getRoot() root} and can be read in
 * {@code O(1)} time via {@link #peek()}.
 *
 * <h2>Shape / balance strategy</h2>
 * A classic array-heap stays a <i>complete</i> tree for free. Because this
 * implementation uses real node objects, it instead keeps the tree balanced by
 * re-running AVL rotations ({@link BinaryNode#toAVL()}) after each insertion.
 * This guarantees a height of {@code O(log n)}, which bounds the cost of the
 * bubble-up and bubble-down passes.
 *
 * <h2>Complexity</h2>
 * <ul>
 *   <li>{@link #peek()} / {@link #isEmpty()} &ndash; {@code O(1)}</li>
 *   <li>{@link #add(Object)} &ndash; {@code O(log n)} amortized (insert on the
 *       right spine, bubble up, then AVL re-balance)</li>
 *   <li>{@link #pop()} &ndash; {@code O(log n)} (detach a deepest leaf, promote
 *       it to the root, then bubble down)</li>
 * </ul>
 *
 * <p>This class is <b>not</b> thread-safe.</p>
 *
 * @param <T> the type of elements held in the heap
 *
 * @author mpoplacenel
 */
public class MinHeap<T> {
	
	/** Root of the backing binary tree; holds the current minimum, or {@code null} when empty. */
	private BinaryNode<T> root;
	
	/** Ordering used to compare elements; defines what "minimum" means. */
	private final Comparator<T> comparator;
	
	/**
	 * Creates an empty heap ordered by the given comparator.
	 *
	 * @param comparator the ordering that defines the heap's minimum; must not be {@code null}
	 */
	public MinHeap(Comparator<T> comparator) {
		this.comparator = comparator;
	}

	/**
	 * Inserts a value into the heap.
	 *
	 * <p>The new node is appended to the tree, bubbled up to restore the heap
	 * invariant, and the tree is then AVL-re-balanced. Runs in {@code O(log n)}.</p>
	 *
	 * @param val the value to insert
	 * @return {@code this}, to allow fluent chaining such as {@code heap.add(1).add(2)}
	 */
	public MinHeap<T> add(final T val) {
		this.root = addNode(new BinaryNode<>(val, null, null));
		return this;
	}

	/**
	 * Removes and returns the smallest element (the root).
	 *
	 * <p>Algorithm: locate a deepest leaf by walking down, preferring the right
	 * child; the root's value is captured as the result; the chosen leaf is
	 * promoted into the root position, inheriting the old root's children; and
	 * finally {@link #bubbleRootDown()} restores the heap invariant. Runs in
	 * {@code O(log n)}.</p>
	 *
	 * @return the previous minimum, or {@code null} if the heap is empty
	 */
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
	
	/**
	 * Returns, without removing, the smallest element.
	 *
	 * @return the current minimum, or {@code null} if the heap is empty
	 */
	public T peek() {
		return this.root == null ? null : this.root.getVal();
	}
	
	/**
	 * @return {@code true} if the heap contains no elements
	 */
	public boolean isEmpty() {
		return this.root == null;
	}

	/**
	 * Package-visible accessor for the backing tree, used by tests to inspect
	 * shape (e.g. height).
	 *
	 * @return the root node, or {@code null} if the heap is empty
	 */
	BinaryNode<T> getRoot() {
		return this.root;
	}

	/**
	 * @return the comparator that defines this heap's ordering
	 */
	Comparator<T> getComparator() {
		return this.comparator;
	}

	/**
	 * Attaches a freshly created node to the tree and restores order.
	 *
	 * <p>The node is appended along the right spine (the first parent on that
	 * spine with a free child slot), bubbled up so the heap invariant holds along
	 * the insertion path, and finally the whole tree is AVL-re-balanced so its
	 * height stays {@code O(log n)}.</p>
	 *
	 * @param node the new single-value node to insert
	 * @return the (possibly new) root after bubbling up and AVL re-balancing
	 */
	BinaryNode<T> addNode(final BinaryNode<T> node) {
		if (this.root == null) {
			return this.root = node;
		}
		List<BinaryNode<T>> path = new ArrayList<>();
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
	
	/**
	 * Swaps a {@code parent} node with one of its (direct) children, rewiring all
	 * child pointers so the {@code child} takes the parent's place in the tree and
	 * vice-versa.
	 *
	 * <p>Only the two nodes' own links are updated; the caller is responsible for
	 * fixing the grandparent's pointer (see {@link #bubbleUp(List)}).</p>
	 *
	 * @param parent the node currently higher in the tree
	 * @param child  a direct child of {@code parent} to swap it with
	 */
	protected void swap(BinaryNode<T> parent, BinaryNode<T> child) {
		BinaryNode<T> left = parent.getLeft();
		BinaryNode<T> right = parent.getRight();
		parent.setLeft(parent == child.getLeft() ? child : child.getLeft());
		parent.setRight(parent == child.getRight() ? child : child.getRight());
		child.setLeft(child == left ? parent : left);
		child.setRight(child == right ? parent : right);
	}

	/**
	 * Restores the heap invariant upward along an insertion path.
	 *
	 * <p>Starting from the just-inserted node (the last element of {@code path})
	 * and walking toward the root, each node is compared with its parent; while it
	 * is smaller it is {@link #swap(BinaryNode, BinaryNode) swapped} up and the
	 * grandparent's pointer is re-linked to it (or {@link #root} is updated when
	 * the top is reached). The moment a parent is not larger, the invariant holds
	 * and the pass stops.</p>
	 *
	 * @param path the chain of nodes from the root down to the newly inserted node,
	 *             in root-to-leaf order
	 */
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

	/**
	 * Restores the heap invariant downward from the root after a {@link #pop()}.
	 *
	 * <p>Repeatedly compares the sinking node with its children: if both children
	 * are not smaller (or it is a leaf) the invariant holds and the pass stops;
	 * otherwise the node is swapped with its smaller child
	 * (chosen via {@link #minNonNull(BinaryNode, BinaryNode)}) and the descent
	 * continues. {@link #root} is updated whenever the topmost node is swapped.</p>
	 */
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
