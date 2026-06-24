package org.callatis.study.solutions;

import static org.junit.Assert.assertArrayEquals;
import org.junit.Before;
import org.junit.Test;

public class ReverseNodesKGroupTest {

    private ReverseNodesKGroup solver;

    @Before
    public void setUp() {
        solver = new ReverseNodesKGroup();
    }

    @Test
    public void testExample0() {
        // Input: head = [1,2,3], k = 2
        // Output: [2,1,3]
        ReverseNodesKGroup.ListNode head = listOf(1, 2, 3);
        int k = 2;

        ReverseNodesKGroup.ListNode result = solver.reverseKGroup(head, k);

        assertArrayEquals(new int[] {2, 1, 3}, toArray(result));
    }

    @Test
    public void testExample1() {
        // Input: head = [1,2,3,4,5], k = 2
        // Output: [2,1,4,3,5]
        ReverseNodesKGroup.ListNode head = listOf(1, 2, 3, 4, 5);
        int k = 2;

        ReverseNodesKGroup.ListNode result = solver.reverseKGroup(head, k);

        assertArrayEquals(new int[] {2, 1, 4, 3, 5}, toArray(result));
    }

    @Test
    public void testExample2() {
        // Input: head = [1,2,3,4,5], k = 3
        // Output: [3,2,1,4,5]
        ReverseNodesKGroup.ListNode head = listOf(1, 2, 3, 4, 5);
        int k = 3;

        ReverseNodesKGroup.ListNode result = solver.reverseKGroup(head, k);

        assertArrayEquals(new int[] {3, 2, 1, 4, 5}, toArray(result));
    }

    @Test
    public void testExample3() {
        // Input: head = [1,2,3,4,5], k = 1
        // Output: [1,2,3,4,5]
        ReverseNodesKGroup.ListNode head = listOf(1, 2, 3, 4, 5);
        int k = 1;

        ReverseNodesKGroup.ListNode result = solver.reverseKGroup(head, k);

        assertArrayEquals(new int[] {1, 2, 3, 4, 5}, toArray(result));
    }

    @Test
    public void testExample4() {
        // Input: head = [1,2], k = 2
        // Output: [2,1]
        ReverseNodesKGroup.ListNode head = listOf(1, 2);
        int k = 2;

        ReverseNodesKGroup.ListNode result = solver.reverseKGroup(head, k);

        assertArrayEquals(new int[] {2, 1}, toArray(result));
    }

    private ReverseNodesKGroup.ListNode listOf(int... values) {
        ReverseNodesKGroup.ListNode dummy = new ReverseNodesKGroup.ListNode(0);
        ReverseNodesKGroup.ListNode current = dummy;
        for (int value : values) {
            current.next = new ReverseNodesKGroup.ListNode(value);
            current = current.next;
        }
        return dummy.next;
    }

    private int[] toArray(ReverseNodesKGroup.ListNode head) {
        int size = 0;
        for (ReverseNodesKGroup.ListNode p = head; p != null; p = p.next) {
            size++;
        }

        int[] result = new int[size];
        int i = 0;
        for (ReverseNodesKGroup.ListNode p = head; p != null; p = p.next) {
            result[i++] = p.val;
        }
        return result;
    }
}
