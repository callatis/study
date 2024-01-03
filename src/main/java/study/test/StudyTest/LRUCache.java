package study.test.StudyTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LRUCache<K,V> {
	
	private final int maxSize;
	
	public LRUCache(int size) {
		this.maxSize = size;
	}
	
	private Map<K, Node<K, V>> map = new HashMap<>();
	private Node<K, V> head;
	private Node<K, V> tail;
	
	public synchronized V getValue(K key) {
		Node<K, V> node = this.map.get(key);
		if (node == null) return null;
		node.setLastAccessTime(new Date());
		moveToHead(node);
		return node.getValue();
	}
	
	public synchronized void addValue(K key, V value) {
		if (this.map.size() >= this.maxSize) { // at capacity, gotta remove
			this.map.remove(this.tail.getKey());
			this.tail = this.tail.getPrev();
			this.tail.setNext(null); // dereference it to be GC-ed
		}
		// add the new node
		Node<K, V> node = new Node<>(key, value);
		if (this.tail != null) this.tail.setNext(node);
		node.setPrev(this.tail);
		if (this.head == null) this.head = node;
		this.tail = node;
		this.map.put(key, node);
	}
	
	@Override
	public synchronized String toString() {
		StringBuilder sb = new StringBuilder("LRUCache [maxSize=").append(maxSize)
				.append(", {");
		Node<K, V> node = this.head;
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
		sb.append("}]");
		return sb.toString();
	}

	private void moveToHead(Node<K, V> node) {
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

	public static class Node<K, V> {
		private final K key;
		private final V value;
		private Date lastAccessTime;
		private Node<K, V> prev = null;
		private Node<K, V> next = null;
		
		public Node(K k, V value) {
			super();
			this.key = k;
			this.value = value;
			this.lastAccessTime = new Date();
		}
		public K getKey() {
			return this.key;
		}
		public V getValue() {
			return this.value;
		}
		public Date getLastAccessTime() {
			return lastAccessTime;
		}
		public void setLastAccessTime(Date date) {
			this.lastAccessTime = date;
		}
		public Node<K, V> getPrev() {
			return prev;
		}
		public void setPrev(Node<K, V> prev) {
			this.prev = prev;
		}
		public Node<K, V> getNext() {
			return next;
		}
		public void setNext(Node<K, V> next) {
			this.next = next;
		}
		@Override
		public String toString() {
			return "Node [key=" + key + ", value=" + value + ", lastAccessTime=" + lastAccessTime + "]";
		}
	}
	
	public static void main(String[] args) {
		test1();
		test2();
	}

	/**
	 * Test 2-members list, with reshuffling and removal. 
	 */
	private static void test1() {
		LRUCache<String, String> cache = new LRUCache<>(2);
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		System.out.println(cache.getValue("two"));
		cache.addValue("three", "trei");
		System.out.println("After eviction: " + cache);
	}
	
	
	/**
	 * Test 3-members list, with reshuffling of mid-element and removal. 
	 */
	private static void test2() {
		LRUCache<String, String> cache = new LRUCache<>(3);
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		cache.addValue("three", "trei");
		System.out.println(cache.getValue("two"));
		cache.addValue("four",  "patru");
		System.out.println("After eviction: " + cache);
	}

}