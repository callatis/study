package org.callatis.study.solutions;

public class DoubleLinkedList<V> {

	private Node<V> head;
	private Node<V> tail;
	
	/**
	 * Create a node with the give value and append it to the end of the list
	 * @param value the value to append.
	 */
	public synchronized Node<V> appendValue(V value) {
		// add the new node
		Node<V> node = new Node<>(value);
		if (this.tail != null) this.tail.setNext(node);
		node.setPrev(this.tail);
		if (this.head == null) this.head = node;
		this.tail = node;
		
		return node;
	}

	public Node<V> getHead() {
		return this.head;
	}

	public Node<V> getTail() {
		return this.tail;
	}

	/**
	 * Remove the tail
	 * @return the removed node. 
	 */
	public Node<V> removeTail() {
		Node<V> node = this.tail;
		if (this.tail == null) {
			return null;
		}
		if (this.head == this.tail) { // one element list
			this.head = this.tail.getPrev();
		}
		this.tail = this.tail.getPrev();
		if (this.tail != null) {
			this.tail.setNext(null); // dereference it to be GC-ed
		}
		
		return node;
	}
	
	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder("DoubleLinkedList [");
		Node<V> node = this.head;
		while (node != null) {
			if (node != this.head) {
				sb.append(", ");
			}
			sb.append(node);
			if (node == this.tail) {
				sb.append("(TAIL)");
			}
			node = node.next;
		}
		sb.append("]");
		return sb.toString();
	}

	public void moveToHead(Node<V> node) {
		if (node.prev == null) return;
		node.prev.next = node.next;
		if (node == this.tail) {
			this.tail = node.prev;
		}
		if (node.next != null) {
			node.next.prev = node.prev;
		}
		node.prev = null;
		node.next = this.head;
		this.head.prev = node;
		this.head = node;
	}

	public static class Node<V> {
		private final V value;
		private Node<V> prev = null;
		private Node<V> next = null;
		
		public Node(V value) {
			super();
			this.value = value;
		}
		public V getValue() {
			return this.value;
		}
		public Node<V> getPrev() {
			return prev;
		}
		public void setPrev(Node<V> prev) {
			this.prev = prev;
		}
		public Node<V> getNext() {
			return next;
		}
		public void setNext(Node<V> next) {
			this.next = next;
		}
		@Override
		public String toString() {
			return "Node [value=" + value + "]";
		}
	}
	
}
