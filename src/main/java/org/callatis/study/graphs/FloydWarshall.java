package org.callatis.study.graphs;

public class FloydWarshall {
	
	/**
	 * Floyd-Warshall sample implementation
	 * @param a graph costs - MAX_INTEGER if there's no edge, 0 if i == j (diagonal). 
	 * @return shortest paths between each two nodes
	 */
	public static int[][] buildMatrix(int[][] a) {
		int[][] adj = new int[a.length][a.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < adj.length; j++) {
				adj[i][j] = a[i][j]; 
			}
		}
		
		for (int k = 0; k < adj.length; k++) {
			for (int i = 0; i < adj.length; i++) {
				for (int j = 0; j < adj.length; j++) {
					if (adj[i][k] < Integer.MAX_VALUE && adj[k][j] < Integer.MAX_VALUE) {
						adj[i][j] = Math.min(adj[i][j], adj[i][k] + adj[k][j]);
					}
				}
			}
		}
		
		return adj;
	}
	
}
