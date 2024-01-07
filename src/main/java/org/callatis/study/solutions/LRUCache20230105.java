package org.callatis.study.solutions;

import java.util.HashMap;
import java.util.Map;

public class LRUCache20230105<K, V> {

	public static void main(String[] args) {
		test1();
		test2();
		test3();
	}

	/**
	 * Test 2-members list, with reshuffling and removal. 
	 */
	private static void test1() {
		LRUCache20230105<String, String> cache = new LRUCache20230105<>(2);
		System.out.println("Should be null: " + cache.getValue("one"));
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		System.out.println("Should be doi: " + cache.getValue("two"));
		
		cache.addValue("three", "trei");
		System.out.println("After eviction: " + cache);
		System.out.println("Should be null: " + cache.getValue("one"));
		System.out.println("Should be trei: " + cache.getValue("three"));
		
		System.out.println("END TEST 1");
	}
	
	
	/**
	 * Test 3-members list, with re-shuffling of mid-element and removal. 
	 */
	private static void test2() {
		LRUCache20230105<String, String> cache = new LRUCache20230105<>(3);
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		cache.addValue("three", "trei");
		System.out.println("Should be doi: " + cache.getValue("two"));
		
		cache.addValue("four",  "patru");
		System.out.println("After eviction: " + cache);
		System.out.println("Should be null: " + cache.getValue("one"));
		System.out.println("Should be patru: " + cache.getValue("four"));
		
		System.out.println("END TEST 2");
	}
	
	/**
	 * Single element list
	 */
	private static void test3() {
		LRUCache20230105<String, String> cache = new LRUCache20230105<>(1);
		cache.addValue("one", "unu");
		cache.addValue("two",  "doi");
		System.out.println("Should be null: " + cache.getValue("one"));
		System.out.println("Should be doi: " + cache.getValue("two"));
		
		cache.addValue("three", "trei");
		System.out.println("After eviction: " + cache);
		System.out.println("Should be trei: " + cache.getValue("three"));

		cache.addValue("three", "trois");
		System.out.println("After eviction: " + cache);
		System.out.println("Should be trois: " + cache.getValue("three"));
		
		System.out.println("END TEST 3");
	}

  
  private Node<K,V> head = null, tail = null;
  private final Map<K, Node<K, V>> map = new HashMap<K, Node<K, V>>();
  private final int maxSize;
  
  public LRUCache20230105(int maxSize) {
    this(maxSize, null);
  }
  
  public LRUCache20230105(int maxSize, Node<K, V> node) {
    if (maxSize < 1) {
      throw new IllegalArgumentException("maxSize < 1");
    }
    this.head = node;
    this.tail = node;
    this.maxSize = maxSize;
  }
  
  public void addValue(K key, V value) { // add a new node to the cache
    if (this.head == null) {
      this.head = new Node<K, V>();
      this.head.key = key;
      this.head.value = value;
      this.tail = this.head;
      this.map.put(key, this.head);
      return;
    }
    // now we know cache was not empty
    Node<K, V> node = this.map.get(key);
    if (node != null) {
      node.value = value;
    } else {
      node = new Node<K, V>();
      node.key = key;
      node.value = value;
      if (this.maxSize == this.map.size()) {
        removeTail();
      }
      this.map.put(key, node);
    }
    
    if (node == this.head) {
      return;
    }
  
    moveToHead(node);
  }
  
  private Node<K, V> removeTail() {
	Node<K, V> oldTail = this.tail;
	if (this.tail == null) return null;
	this.map.remove(oldTail.key);
	if (this.tail.prev == null) {
		if (this.head != this.tail) {
			throw new IllegalStateException("Tail's prev is null but there's a different head");
		}
		this.head = this.tail = null;
		return null;
	}
	this.tail.prev.next = null;
	this.tail = this.tail.prev;
	
	return oldTail;
}

public V getValue(K key) {
    Node<K, V> node = this.map.get(key);
    if (node != null && node != this.head) {
      moveToHead(node);
    }

    return node == null ? null : node.value;
  }
  
  private void moveToHead(Node<K, V> node) {
    // connect its previous to its next
    // and position the tail to it if it was the former tail
    if (node.prev != null) {
      if (node == this.tail) {
        this.tail = node.prev;        
      }
      node.prev.next = node.next;
    }
    if (node.next != null) {
    	node.next.prev = node.prev;
    }
    node.prev = null;
    node.next = this.head;
    if (this.head != null) {
    	this.head.prev = node;
    }
    this.head = node;
  }
  
  public String toString() {
	  if (this.head == null) {
		  return "[]";
	  }
	  StringBuilder sb = new StringBuilder().append("[");
	  for (Node<K, V> node = this.head; node != null; node = node.next) {
		  if (node != this.head) {
			  sb.append(", ");
		  }
		  sb.append(node);
	  }
	  return sb.append("]").toString();
  }

	private static class Node<K, V> {
		
		  public K key = null;
		  public  V value = null;
		  public Node<K, V> next, prev;
		  
		  public String toString() {
			  return this.key + "=" + this.value;
		  }
		}
	
}



// Your last JavaScript (Node) code is saved below:
// //  LRU Cache Implementation
// //      entry             entry             entry             entry
// //      ______            ______            ______            ______
// //     | head |.older => |      |.older => |      |.older => | tail |
// //     |  A   |          |  B   |          |  C   |          |  D   |
// //     |______| <= newer.|______| <= newer.|______| <= newer.|______|
// //
// //  added  -->  -->  -->  -->  -->  -->  -->  -->  -->  -->  -->  removed
// //  Head (Most Recently Used)                                Tail (Least Recently Used)
// //

// // We'd like to implement a caching class to provide a Least Recently Used cache with a max capacity
// // Can you think of potential uses for such a cache?
// // What performance considerations might need addressing?


// // Your last Plain Text code is saved below:
// }

