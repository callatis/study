package org.callatis.study.solutions;

import java.util.HashMap;
import java.util.Map;

public class LRUCacheLC {

   private static class Node {

        private int key;
        private int val;
        private Node next;
        private Node prev;

        private Node(int key) {
            this.key = key;
        }

        /**
         * Moves this node before the given one
         * @param node the one before which to move
         */
        public void moveBefore(Node node) {
            if (this == node) {
                throw new IllegalArgumentException("Cannot move before itself");
            }
            if (this.next == node) { // alread in the right position
                return;
            }
            if (this.prev != null) {
                this.prev.next = this.next;
            }
            if (this.next != null) {
                this.next.prev = this.prev;
            }
            if (node.prev != null) {
                node.prev.next = this;
            }
            this.prev = node.prev;
            node.prev = this;
            this.next = node;
        }

        @Override
        public String toString() {
            return "{ key = " + this.key + ", value = " + this.val/* + ", next = " + this.next */ + " }";
        }
    }

    private Node head = null, tail = null; 

    private final Map<Integer, Node> map;

    private final int capacity;

    public LRUCacheLC(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>(capacity);
    }
    
    public int get(int key) {
        System.out.println("get(" + key + ")");
        Node node = this.map.get(key);
        if (node == null) return -1;

        System.out.println("Found: " + node);
        if (node != this.head) {
            if (node == this.tail) {
                this.tail = this.tail.prev;
            }
            node.moveBefore(this.head);
        }
        System.out.println("head: " + node);
        return node.val;
    }
    
    public void put(int key, int value) {
        System.out.println("put(" + key + ", " + value + ")");
        Node node = this.map.get(key);
        if (node == null) {
            node = new Node(key);
            this.map.put(key, node);
            evictLast();
        }
        node.val = value;
        node.next = this.head;
        if (this.head != null) {
            this.head.prev = node;
            if (this.tail == node) {
                this.tail = this.tail.prev;
            }
            node.prev = null;
        } else {
            if (this.tail == null) this.tail = node;

        }
        this.head = node;
    }

    private void evictLast() {
        if (this.map.size() > this.capacity) { // we added one too many
            System.out.println("Evict as " + this.map.size() + " > " + this.capacity);
            Node last = this.tail;
            this.tail = last.prev;
            if (this.tail != null) {
                this.tail.next = null;
            } else {
                this.head = null;
            }
            System.out.println("Evict " + last.key);
            this.map.remove(last.key);
        }
    }

}

/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache obj = new LRUCache(capacity);
 * int param_1 = obj.get(key);
 * obj.put(key,value);
 */
