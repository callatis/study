/**
 * 
 */
package org.callatis.study.solutions;

import java.util.Stack;

import junit.framework.Assert;

/**
 * @author mishe
 *
 */
public class OptimalListPalindromeQualifier {

	/**
	 * Definition for singly-linked list.
	 * public class ListNode {
	 *     int val;
	 *     ListNode next;
	 *     ListNode() {}
	 *     ListNode(int val) { this.val = val; }
	 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
	 * }
	 */
    public boolean isPalindrome(ListNode head) {
    	ListNode slow = head, fast = head;
    	while (fast != null && fast.next != null) {
    		slow = slow.next;
    		fast = fast.next.next;
    	}
    	// slow now points to the middle of the list
    	
    	// reverse the second half of the list, in place
		ListNode temp = slow;
		while (temp != null) {
			fast = new ListNode(temp.val, fast);
			temp = temp.next;
		}
		
		// compare the two halves
    	while (head != slow) {
    		if (head.val != fast.val) return false;
    		head = head.next;
    		fast = fast.next;
    	}

        return true;
    }
    
    private ListNode reverse(ListNode head) {
    	ListNode node = null;
    	while (head != null) {
    		node = new ListNode(head.val, node);
    		head = head.next;
    	}
    	
    	return node;
    }

    private ListNode reverseRecursive(ListNode head, ListNode tail) {
    	if (head == null) return tail;
    	tail = new ListNode(head.val, tail);
    	
    	return reverseRecursive(head.next, tail);
    }

    /*
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.assertTrue(new OptimalListPalindromeQualifier().isPalindrome(createLinkedList1()));
		Assert.assertTrue(new OptimalListPalindromeQualifier().isPalindrome(createLinkedList2()));
		Assert.assertFalse(new OptimalListPalindromeQualifier().isPalindrome(createLinkedList3()));
		Assert.assertFalse(new OptimalListPalindromeQualifier().isPalindrome(createLinkedList4()));
		Assert.assertTrue((new OptimalListPalindromeQualifier().isPalindrome(createLinkedList5())));
		System.out.println("PASSED");
	}

	/**
	 * [1, 2, 3, 2, 1] 
	 */
	private static ListNode createLinkedList1() {
		ListNode node = new ListNode(1);
		node = new ListNode(2, node);
		node = new ListNode(3, node);
		node = new ListNode(2, node);
		node = new ListNode(1, node);
		
		return node;
	}

	/**
	 * [1, 2, 2, 1] 
	 */
	private static ListNode createLinkedList2() {
		ListNode node = new ListNode(1);
		node = new ListNode(2, node);
		node = new ListNode(2, node);
		node = new ListNode(1, node);
		
		return node;
	}

	/**
	 * [1, 2, 1, 1] 
	 */
	private static ListNode createLinkedList3() {
		ListNode node = new ListNode(1);
		node = new ListNode(2, node);
		node = new ListNode(1, node);
		node = new ListNode(1, node);
		
		return node;
	}

	/**
	 * [1, 2] 
	 */
	private static ListNode createLinkedList4() {
		ListNode node = new ListNode(1);
		node = new ListNode(2, node);
		
		return node;
	}

	/**
	 * [1, 2] 
	 */
	private static ListNode createLinkedList5() {
		ListNode node = new ListNode(1);
		
		return node;
	}

}
