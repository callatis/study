/**
 * 
 */
package study.test.StudyTest;

import java.util.Stack;

/**
 * @author mishe
 *
 */
public class ListPalindromeQualifier {

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
        int len = calcLen(head);
        Stack<Integer> stack = new Stack<>();
        ListNode node = head;
        for (int i = 0; i < len / 2; node = node.next, i++) {
            stack.push(node.val);
        }

        if (len % 2 == 1) { // odd length
            // go past the middle node
            node = node.next;
        }
        for (int i = 0; i < len / 2; node = node.next, i++) {
            Integer rightVal = stack.pop();
            if (node.val != rightVal) {
                return false;
            }
        }

        return true;
    }

    private int calcLen(ListNode node) {
        if (node == null) return 0;
        return 1 + calcLen(node.next);
    }
    
    /*
	 * @param args
	 */
	public static void main(String[] args) {
		ListNode head = createLinkedList();
		ListPalindromeQualifier sol = new ListPalindromeQualifier();
		System.out.println("IsPalindrome: " + sol.isPalindrome(head));
	}

	/**
	 * 
	 */
	private static ListNode createLinkedList() {
		ListNode node = new ListNode(1);
		node = new ListNode(2, node);
		node = new ListNode(3, node);
		node = new ListNode(2, node);
		node = new ListNode(1, node);
		
		return node;
	}

}
