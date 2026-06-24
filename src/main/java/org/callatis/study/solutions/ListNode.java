package org.callatis.study.solutions;

public class ListNode {

    /* package */ int val;
    /* package */ ListNode next;
    /* package */ ListNode(int val) { this.val = val; }
    /* package */ ListNode(int val, ListNode next) { this.val = val; this.next = next; }

    /* package */ ListNode skipN(int k) {
        ListNode p = this;
        for (int i = 0; i < k; i++) {
            if (p.next == null) {
                return null;
            }
            p = p.next;
        }
        return p;
    }

    public ListNode removeKNode(int k) {
        if (k <= 0) throw new IllegalArgumentException("k = " + k + " must be > 0");
        ListNode prev = skipN(k - 1); // q will be the previous (k-1) node, where node 0 is `this`
        if (prev == null) { // not even a previous - should we throw IllegalArgument? 
            return null;
        }
        ListNode node = prev.next; // the node to remove
        if (node == null) { // end of list - nothing to remove
            return null;
        }
        prev.next = node.next; // skip it in the chain

        node.next = null; // reset the pointer
        return node;
    }

    public ListNode removeFirst() {
        return removeKNode(1);
    }

    public ListNode insert(ListNode p) {
        ListNode prevNext = p.next; // save this to return it
        ListNode theNext = this.next;
        this.next = p;
        p.next = theNext;

        return prevNext;
    }

}
