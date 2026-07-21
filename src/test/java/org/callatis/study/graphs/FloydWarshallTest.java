package org.callatis.study.graphs;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Unit tests for {@link FloydWarshall#buildMatrix(int[][])}.
 *
 * <p>Each test builds a small symmetric (undirected) graph, runs the
 * all-pairs shortest path computation, and verifies that the diagonal is
 * zero and that every pair resolves to the expected shortest distance,
 * including cases where the shortest path goes through an intermediate node.</p>
 */
public class FloydWarshallTest {

	/**
	 * Verifies that a path through an intermediate node is preferred over the
	 * (missing) direct edge. Nodes 0 and 2 have no direct edge, so the shortest
	 * distance {@code 6} must be found via node 1 ({@code 2 + 4}).
	 */
	@Test
	public void testBuildMatrix() {
		int[][] a = new int[][] {
				{ 0, 2, Integer.MAX_VALUE}, 
				{ 2, 0, 4 },
				{ Integer.MAX_VALUE, 4, 0}
		};
		int[][] adj = FloydWarshall.buildMatrix(a);
		for (int i = 0; i < adj.length; i++) {
			assertEquals("At: " + i + ": ", 0, adj[i][i]);
		}
		assertEquals(2, adj[0][1]);
		assertEquals(2, adj[1][0]);

		assertEquals(4, adj[1][2]);
		assertEquals(4, adj[2][1]);

		assertEquals(6, adj[0][2]);
		assertEquals(6, adj[2][0]);

	}

	/**
	 * Verifies that the direct edge is kept when it is shorter than any
	 * detour. Here the direct edge {@code 0 -> 2} has weight {@code 2}, which is
	 * cheaper than routing through node 1 ({@code 1 + 5 = 6}); the shortest
	 * {@code 1 -> 2} distance instead improves to {@code 3} via node 0.
	 */
	@Test
	public void testBuildMatrix2() {
		int[][] a = new int[][] {
				{ 0, 1, 2}, 
				{ 1, 0, 5 },
				{ 2, 5, 0}
		};
		int[][] adj = FloydWarshall.buildMatrix(a);
		for (int i = 0; i < adj.length; i++) {
			assertEquals("At: " + i + ": ", 0, adj[i][i]);
		}
		assertEquals(1, adj[0][1]);
		assertEquals(1, adj[1][0]);

		assertEquals(3, adj[1][2]);
		assertEquals(3, adj[2][1]);

		assertEquals(2, adj[0][2]);
		assertEquals(2, adj[2][0]);

	}

}
