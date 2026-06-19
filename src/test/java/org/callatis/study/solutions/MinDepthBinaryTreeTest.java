package org.callatis.study.solutions;

import org.callatis.study.solutions.MinDepthBinaryTree.TreeNode;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class MinDepthBinaryTreeTest {

    private MinDepthBinaryTree minDepth;

    @Before
    public void setUp() {
        minDepth = new MinDepthBinaryTree();
    }

    @Test
    public void testExample1() {
        // Input: root = [3,9,20,null,null,15,7]
        // Output: 2
        //       3
        //      / \
        //     9  20
        //        / \
        //       15   7
        TreeNode root = new TreeNode(3,
            new TreeNode(9),
            new TreeNode(20, new TreeNode(15), new TreeNode(7))
        );
        assertEquals(2, minDepth.minDepthDFSRecursive(root));
    }

    @Test
    public void testExample2() {
        // Input: root = [2,null,3,null,4,null,5,null,6]
        // Output: 5
        // 2
        //  \
        //   3
        //    \
        //     4
        //      \
        //       5
        //        \
        //         6
        TreeNode root = new TreeNode(2, null,
            new TreeNode(3, null,
                new TreeNode(4, null,
                    new TreeNode(5, null,
                        new TreeNode(6)))));
        assertEquals(5, minDepth.minDepthDFSRecursive(root));
    }

    @Test
    public void testNullRoot() {
        // Constraint: number of nodes in range [0, 10^5], so empty tree is valid
        assertEquals(0, minDepth.minDepthDFSRecursive(null));
    }

    @Test
    public void testSingleNode() {
        TreeNode root = new TreeNode(1);
        assertEquals(1, minDepth.minDepthDFSRecursive(root));
    }

    // --- BFS Iterative ---

    @Test
    public void testBFSExample1() {
        // Input: root = [3,9,20,null,null,15,7]
        // Output: 2
        //       3
        //      / \
        //     9  20
        //        / \
        //       15   7
        TreeNode root = new TreeNode(3,
            new TreeNode(9),
            new TreeNode(20, new TreeNode(15), new TreeNode(7))
        );
        assertEquals(2, minDepth.minDepthBFSIterative(root));
    }

    @Test
    public void testBFSExample2() {
        // Input: root = [2,null,3,null,4,null,5,null,6]
        // Output: 5
        // 2
        //  \
        //   3
        //    \
        //     4
        //      \
        //       5
        //        \
        //         6
        TreeNode root = new TreeNode(2, null,
            new TreeNode(3, null,
                new TreeNode(4, null,
                    new TreeNode(5, null,
                        new TreeNode(6)))));
        assertEquals(5, minDepth.minDepthBFSIterative(root));
    }

    @Test
    public void testBFSNullRoot() {
        assertEquals(0, minDepth.minDepthBFSIterative(null));
    }

    @Test
    public void testBFSSingleNode() {
        TreeNode root = new TreeNode(1);
        assertEquals(1, minDepth.minDepthBFSIterative(root));
    }

    // --- BFS Level-by-Level ---

    @Test
    public void testBFSLevelsExample1() {
        // Input: root = [3,9,20,null,null,15,7]
        // Output: 2
        //       3
        //      / \
        //     9  20
        //        / \
        //       15   7
        TreeNode root = new TreeNode(3,
            new TreeNode(9),
            new TreeNode(20, new TreeNode(15), new TreeNode(7))
        );
        assertEquals(2, minDepth.minDepthBFSIterateLevels(root));
    }

    @Test
    public void testBFSLevelsExample2() {
        // Input: root = [2,null,3,null,4,null,5,null,6]
        // Output: 5
        // 2
        //  \
        //   3
        //    \
        //     4
        //      \
        //       5
        //        \
        //         6
        TreeNode root = new TreeNode(2, null,
            new TreeNode(3, null,
                new TreeNode(4, null,
                    new TreeNode(5, null,
                        new TreeNode(6)))));
        assertEquals(5, minDepth.minDepthBFSIterateLevels(root));
    }

    @Test
    public void testBFSLevelsNullRoot() {
        assertEquals(0, minDepth.minDepthBFSIterateLevels(null));
    }

    @Test
    public void testBFSLevelsSingleNode() {
        TreeNode root = new TreeNode(1);
        assertEquals(1, minDepth.minDepthBFSIterateLevels(root));
    }
}
