package org.callatis.study.graphs;

/**
 * Solves the all-pairs shortest path problem on a weighted graph using the
 * Floyd-Warshall algorithm.
 *
 * <p><b>Problem.</b> Given a directed or undirected graph of {@code n} nodes
 * with (possibly negative) edge weights, compute the length of the shortest
 * path between every ordered pair of nodes {@code (i, j)}. Unlike single-source
 * algorithms such as Dijkstra or Bellman-Ford, this computes the shortest
 * distance for <em>all</em> pairs at once.</p>
 *
 * <p><b>Algorithm.</b> Floyd-Warshall is a dynamic programming approach. It
 * considers each node {@code k} in turn as a potential intermediate node on the
 * path between {@code i} and {@code j}, and relaxes the distance:
 * {@code dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])}. After all nodes
 * have been considered as intermediates, {@code dist[i][j]} holds the shortest
 * path length. Runs in {@code O(n^3)} time and {@code O(n^2)} space.</p>
 *
 * <p><b>Conventions.</b> The input adjacency matrix uses
 * {@link Integer#MAX_VALUE} to represent the absence of an edge (infinite cost)
 * and {@code 0} on the diagonal (distance from a node to itself). The algorithm
 * assumes no negative cycles; with negative cycles the resulting distances are
 * not well defined.</p>
 */
public class FloydWarshall {
	
	/**
	 * Computes the all-pairs shortest path matrix for the given weighted graph.
	 *
	 * <p>The input is left unmodified; the result is computed on a defensive
	 * copy. Each cell {@code result[i][j]} of the returned matrix contains the
	 * length of the shortest path from node {@code i} to node {@code j}, or
	 * {@link Integer#MAX_VALUE} if no path exists.</p>
	 *
	 * <p>The guard {@code adj[i][k] < Integer.MAX_VALUE && adj[k][j] < Integer.MAX_VALUE}
	 * ensures that unreachable pairs are skipped and prevents integer overflow
	 * when adding two "infinite" costs.</p>
	 *
	 * @param a graph cost matrix; {@code a[i][j]} is the weight of the edge from
	 *          {@code i} to {@code j}, {@link Integer#MAX_VALUE} if there is no
	 *          edge, and {@code 0} on the diagonal ({@code i == j})
	 * @return a new {@code n x n} matrix of shortest path lengths between every
	 *         pair of nodes
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
