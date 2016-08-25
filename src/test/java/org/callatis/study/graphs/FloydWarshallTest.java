package org.callatis.study.graphs;

import static org.junit.Assert.*;

import org.callatis.study.graphs.FloydWarshall;
import org.junit.Test;

public class FloydWarshallTest {

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
