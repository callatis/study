package org.callatis.study.solutions;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.callatis.study.solutions.DoubleLinkedList.Node;

public class LRUCache2<K,V> {
	
	private final int maxSize;
	
	public LRUCache2(int size) {
		this.maxSize = size;
	}
	
	private Map<K, Node<NodeValue<K, V>>> map = new HashMap<>();
	private DoubleLinkedList<NodeValue<K, V>> doubleLinkedList = new DoubleLinkedList<>();
	
	public synchronized V getValue(K key) {
		Node<NodeValue<K, V>> node = this.map.get(key);
		if (node == null) {
			return null;
		}
		node.getValue().setLastAccessTime(new Date());
		this.doubleLinkedList.moveToHead(node);
		return node.getValue().getValue();
	}
	
	public synchronized void addValue(K key, V value) {
		if (this.map.size() >= this.maxSize) { // at capacity, gotta remove
			Node<NodeValue<K, V>> oldTail = this.doubleLinkedList.removeTail();
			this.map.remove(oldTail.getValue().getKey());
		}
		// add the new node
		Node<NodeValue<K, V>> node = this.doubleLinkedList.appendValue(new NodeValue<>(key, value));
		this.map.put(key, node);
	}
	
	@Override
	public synchronized String toString() {
		return "LRUCache [maxSize=" + maxSize
				+ ", doubleLinkedList = " + this.doubleLinkedList;
	}

	public static class NodeValue<K, V> {
		private final K key;
		private final V value;
		private Date lastAccessTime;
		
		public NodeValue(K key, V value) {
			super();
			this.key = key;
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
		@Override
		public String toString() {
			return "NodeValue [key=" + key + ", value=" + value + ", lastAccessTime=" + lastAccessTime + "]";
		}
	}
	
	public static void main(String[] args) {
		test1();
		test2();
		test3();
	}

	/**
	 * Test 2-members list, with reshuffling and removal. 
	 */
	private static void test1() {
		LRUCache2<String, String> cache = new LRUCache2<>(2);
		System.out.println("Should be null: " + cache.getValue("one"));
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		System.out.println("Should be doi: " + cache.getValue("two"));
		cache.addValue("three", "trei");
		System.out.println("After eviction: " + cache);
		System.out.println("Should be null: " + cache.getValue("one"));
		System.out.println("Should be trei: " + cache.getValue("three"));
	}
	
	
	/**
	 * Test 3-members list, with reshuffling of mid-element and removal. 
	 */
	private static void test2() {
		LRUCache2<String, String> cache = new LRUCache2<>(3);
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		cache.addValue("three", "trei");
		System.out.println("Should be doi: " + cache.getValue("two"));
		cache.addValue("four",  "patru");
		System.out.println("After eviction: " + cache);
		System.out.println("Should be null: " + cache.getValue("three"));
		System.out.println("Should be patru: " + cache.getValue("four"));
	}
	
	/**
	 * Single element list
	 */
	private static void test3() {
		LRUCache2<String, String> cache = new LRUCache2<>(1);
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		System.out.println("Should be null: " + cache.getValue("one"));
		System.out.println(cache.getValue("two"));
		cache.addValue("three", "trei");
		System.out.println("After eviction: " + cache);
		System.out.println("Should be trei: " + cache.getValue("three"));
	}

}