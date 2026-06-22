package org.callatis.study.solutions;

import java.util.Stack;

public class ReverseNodesKGroup {

    public static class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    private ListNode skipN(ListNode p, int k) {
        for (int i = 0; i < k; i++) {
            if (p.next == null) {
                return null;
            }
            p = p.next;
        }

        return p;
    }

    private ListNode swapInPlace(ListNode startP, ListNode endP) {
        return null;
    }

    private ListNode swapWithStack(ListNode startP, ListNode endP) {
        Stack<ListNode> stack = new Stack<>();
        // push all nodes between startP and right-before endP onto the stack.
        ListNode p;
        for (p = startP; p != null && p != endP; p = p.next) {
            stack.push(p);
        }
        // if we reached the end of the list prematurely, i.e. before reaching endP, should be an error. 
        if (p == null) throw new IllegalStateException("Reached end of list before the end of the group");
        // so now we know p == endP
        // store the reference to the next node after this group
        ListNode endPNext = endP.next;
        ListNode pNext = null;
        while (!stack.isEmpty()) {
            pNext = stack.pop();
            p.next = pNext;
            p = pNext;
        }
        // set the saved reference to the next node after the group
        if (pNext != null) {
            pNext.next = endPNext;
        }

        return endP;
    }

    public ListNode reverseKGroup(ListNode head, int k) {

        ListNode startP = head, endP = skipN(startP, k - 1), dummy = new ListNode(0, head), nextP;
        ListNode prevP = dummy;
        while (endP != null) {
            nextP = endP.next;
            ListNode newStartP = swapWithStack(startP, endP); // the old endP
            prevP.next = newStartP; // we connect to the left part of the list
            // startP = endP;
            prevP = startP; // the old start is now the end, should point to nextP
            // prevP.next = nextP; not needed, was connected inside the helper method
            startP = nextP;
            endP = startP == null ? null : skipN(startP, k - 1);
        }

        return dummy.next;
    }

}
