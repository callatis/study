package org.callatis.study.solutions;

import java.util.Stack;

public class ReverseNodesKGroup {

    ListNode reverseKGroup(ListNode head, int k) {
        return reverseKGroupInPlace(head, k);
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

    private ListNode reverseKGroupWithStack(ListNode head, int k) {

        ListNode startP = head, endP = startP.skipN(k - 1), dummy = new ListNode(0, head), nextP;
        ListNode prevP = dummy;
        while (endP != null) {
            nextP = endP.next;
            ListNode newStartP = swapWithStack(startP, endP); // the old endP
            prevP.next = newStartP; // we connect to the left part of the list
            // startP = endP;
            prevP = startP; // the old start is now the end, should point to nextP
            // prevP.next = nextP; not needed, was connected inside the helper method
            startP = nextP;
            endP = startP == null ? null : startP.skipN(k - 1);
        }

        return dummy.next;
    }

    private ListNode swapInPlace(ListNode dummy, int k) {
        ListNode last = dummy.next;
        // skip k nodes
        for (int i = 0; i < k && last != null; i++) {
            last = last.next;
        }
        if (last == null) {
            return null;
        }
        ListNode origLast = last;
        
        while (dummy.next != origLast) { // could be i = 0, ..., k
            ListNode curr = dummy.next; // repoint to the current head
            dummy.next = curr.next; // unplug it
            ListNode afterLast = origLast.next; // store it to avoid being overwritten
            origLast.next = curr; // plug it right after the last; afterLast is now dangling
            curr.next = afterLast;
            if (last == origLast) { // first time the loop is evaluated - first element becomes last of the k
                last = curr;
            }
        }

        return last;
    }

    private ListNode reverseKGroupInPlace(ListNode head, int k) {
        ListNode dummy = new ListNode(0, head);
        ListNode prevP = dummy;
        do { 
            prevP = swapInPlace(prevP, k - 1);
        } while (prevP != null);

        return dummy.next;
    }

}
