package org.callatis.study.solutions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class MinDepthBinaryTree {
    public static class TreeNode {
        private int val;
        private TreeNode left;
        private TreeNode right;

        public TreeNode() {}

        public TreeNode(int val) {
            this.val = val;
        }

        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    public static class MyQ<T> {
        private final List<T> queue = new ArrayList<>();
        
        public void add(T t) {
            queue.add(t);
        }
        
        public T pop() {
            return queue.removeFirst();
        }
    }

    public static class Pair<X, Y> {
        public X x;
        public Y y;
        public Pair(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

   public int minDepthDFSRecursive(TreeNode root) {
        if (root == null) {
            return 0;
        }
        if (root.left == null) {
            return 1 + minDepthDFSRecursive(root.right);
        } else if(root.right == null) {
            return 1 + minDepthDFSRecursive(root.left);
        } else {
            return 1 + Math.min(minDepthDFSRecursive(root.left), minDepthDFSRecursive(root.right));
        }
    }

    public int minDepthBFSIterative(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Queue<Pair<TreeNode, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(root, 0));
        while (!q.isEmpty()) {
            Pair<TreeNode, Integer> first = q.remove();
            TreeNode node = first.x;
            if (node.left != null) {
                q.add(new Pair<>(node.left, first.y + 1));
            }
            if (node.right != null) {
                q.add(new Pair<>(node.right, first.y + 1));
            }
            if (node.left == null && node.right == null) {
                return first.y + 1;
            }
        }

        return -1;
    }

    public int minDepthBFSIterateLevels(TreeNode root) {
        if (root == null) {
            return 0;
        }
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        int level = 1;
        while (!q.isEmpty()) {
            int levelSize = q.size(); // # of nodes for the current level
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = q.remove();
                if (node.left == null && node.right == null) {
                    // found a leaf at this level
                    return level;
                }
                if (node.left != null) {
                    q.add(node.left);
                }
                if (node.right != null) {
                    q.add(node.right);
                }
            }
            level++;
        }

        return -1;
    }

}