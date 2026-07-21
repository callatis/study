package org.callatis.study.graphs;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Solution for the <a href="https://community.topcoder.com/stat?c=problem_statement&rd=4725&pm=2288">KiloManX</a>
 * TopCoder problem.
 *
 * <h2>Problem</h2>
 * <p>
 * In the spirit of the <i>Mega Man</i> games, the hero must defeat a set of {@code n} bosses.
 * The hero always starts with a default weapon (the "buster") that deals exactly {@code 1} point
 * of damage to every boss. Whenever a boss is defeated, the hero acquires that boss's weapon,
 * which can then be used against the remaining bosses.
 * </p>
 * <p>
 * The bosses and their weapons are described by a square {@code damageChart}: the character
 * {@code damageChart[i].charAt(j)} is a digit {@code '0'..'9'} giving the amount of damage that the
 * weapon obtained from boss {@code i} inflicts on boss {@code j} with a single shot. A value of
 * {@code 0} means that weapon is useless against boss {@code j}. Each boss {@code i} starts with
 * {@code bossHealth[i]} hit points, and a boss is defeated once its remaining health reaches zero
 * or below.
 * </p>
 * <p>
 * A shot always removes a whole number of hit points, so the number of shots required to kill a
 * boss with a weapon that deals {@code d} damage is {@code ceil(bossHealth / d)}. The goal is to
 * choose the order in which the bosses are defeated so as to minimize the <b>total</b> number of
 * shots fired across all bosses.
 * </p>
 *
 * <h2>Solution</h2>
 * <p>
 * The set of bosses already defeated (equivalently, the set of weapons already owned) is encoded as
 * a bitmask over {@code n} bits, so there are {@code 2^n} possible states. This turns the puzzle
 * into a shortest-path problem on a graph whose vertices are these {@code 2^n} states:
 * </p>
 * <ul>
 *   <li>the start vertex is the empty mask {@code 0} (no boss defeated) with cost {@code 0};</li>
 *   <li>from a state, defeating a not-yet-killed boss {@code i} transitions to the state
 *       {@code mask | (1 << i)}, adding the minimum number of shots needed to kill boss {@code i}
 *       using the best weapon currently owned (falling back to the default weapon, which costs
 *       {@code bossHealth[i]} shots);</li>
 *   <li>the target vertex is the full mask {@code 2^n - 1} (all bosses defeated).</li>
 * </ul>
 * <p>
 * Because all edge weights are non-negative, the shortest path is found with Dijkstra's algorithm.
 * A {@link TreeSet} ordered by {@link NodeComparator} acts as the priority queue, always expanding
 * the reachable state with the fewest accumulated shots, and a {@code visited} array guarantees each
 * state is finalized at most once. The cost recorded when the full mask is first popped is optimal
 * and is returned.
 * </p>
 *
 * @author mpoplacenel
 */
public class KiloManX {
	
	/**
	 * Computes the minimum total number of shots required to defeat every boss.
	 *
	 * <p>
	 * Runs Dijkstra's algorithm over the {@code 2^n} "set of defeated bosses" states (see the class
	 * documentation for the full model). For each unvisited state pulled from the priority queue, the
	 * method tries to defeat each remaining boss {@code i} using the cheapest available weapon: it
	 * starts from the default weapon cost ({@code bossHealth[i]} shots at 1 damage each) and improves
	 * it with any owned weapon {@code j} that deals positive damage to boss {@code i}, using
	 * {@code ceil(bossHealth[i] / damage)} shots.
	 * </p>
	 *
	 * @param damageChart a square {@code n x n} chart of digit characters where
	 *                    {@code damageChart[i].charAt(j)} is the per-shot damage that boss
	 *                    {@code i}'s weapon deals to boss {@code j}
	 * @param bossHealth  the starting hit points of each of the {@code n} bosses
	 * @return the least number of shots needed to defeat all bosses, or {@code -1} if no order
	 *         defeats them all
	 * @throws IllegalArgumentException if {@code bossHealth.length} differs from
	 *                                  {@code damageChart.length}, or if any row of
	 *                                  {@code damageChart} is not exactly {@code n} characters long
	 */
	public static int leastShots(String[] damageChart, int[] bossHealth) {
		int n = damageChart.length;
		if (bossHealth.length != n) {
			throw new IllegalArgumentException("Must be same size: " + n + " != " + bossHealth.length);
		}
		for (int i = 0; i < damageChart.length; i++) {
			if (damageChart[i].length() != n) {
				throw new IllegalArgumentException(
						"damageChart[" + i + "].length() = " + damageChart[i].length() + " != " + n);
			}
		}
		SortedSet<Node> nodeSet = new TreeSet<>(new NodeComparator());
		for (int i = 0; i < n; i++) {
			nodeSet.add(new Node(bossHealth[i], 1 << i));
		}
		boolean[] visited = new boolean[(int) Math.pow(2, n)];
		visited[0] = true;
		while (!nodeSet.isEmpty()) {
			Node top = nodeSet.first();
			nodeSet.remove(top);
			if (top.getWeapons() == ((int) Math.pow(2, n) - 1)) { // found the node
				return top.shots;
			}
			if (visited[top.getWeapons()]) {
				continue;
			}
			visited[top.getWeapons()] = true;
			for (int i = 0; i < n; i++) { // check all bosses not yet killed
				if ((top.getWeapons() & (1 << i)) > 0) { // boss already killed
					continue;
				}
				int best = bossHealth[i];
				// for all weapons we have to kill #i... 
				for (int j = 0; j < n; j++) {
					if (i == j) continue; // i not dead, obviously no weapon
					if (((top.getWeapons() & (1 << j)) > 0)  // we have the weapon
							&& ((damageChart[j].charAt(i) - '0') > 0)) { // the weapon has effect on him
						int newBest = bossHealth[i] / (damageChart[j].charAt(i) - '0');
						if (bossHealth[i] % (damageChart[j].charAt(i) - '0') > 0) { // remainder, fire one more shot
							newBest++;
						}
						best = Math.min(newBest, best);
					}
				}
				
				nodeSet.add(new Node(top.getShots() + best, top.getWeapons() | (1 << i)));
			}
		}
		
		return -1;
	}
	
	/**
	 * Orders {@link Node} states for the Dijkstra priority queue: primarily by ascending accumulated
	 * {@link Node#getShots() shots}, and secondarily by ascending {@link Node#getWeapons() weapons}
	 * bitmask.
	 *
	 * <p>
	 * The secondary key breaks ties deterministically (weapons are typically filled from bit 0
	 * upwards) so that distinct nodes with the same shot count are not treated as equal and therefore
	 * survive together in a {@link TreeSet}. {@code null} is considered smaller than any non-null
	 * node.
	 * </p>
	 */
	public static class NodeComparator implements Comparator<Node> {

		/**
		 * Compares two nodes by shots first, then by weapons bitmask.
		 *
		 * @param o1 the first node, may be {@code null}
		 * @param o2 the second node, may be {@code null}
		 * @return a negative value, zero, or a positive value as {@code o1} is ordered before, equal
		 *         to, or after {@code o2}
		 */
		@Override
		public int compare(Node o1, Node o2) {
			if (o1 == null) return o2 == null ? 0 : -1; // null == null, null < !null
			if (o2 == null) return 1; // o1 not null, hence o1 > o2
			int shotsDelta = o1.getShots() - o2.getShots();
			if (shotsDelta != 0) {
				return (int) Math.signum(shotsDelta);
			}
			// favor the ones with less in 'weapons', as we're typically filling the 
			// weapons from 0 upwards
			return (int) Math.signum(o1.getWeapons() - o2.getWeapons());
		}
		
	}

	/**
	 * A search state in the Dijkstra traversal, pairing the set of bosses already defeated with the
	 * total number of shots spent to reach that set.
	 *
	 * <p>
	 * The {@code weapons} field is a bitmask over the {@code n} bosses: bit {@code i} is set when boss
	 * {@code i} has been defeated (and therefore its weapon is owned). The {@code shots} field is the
	 * accumulated cost of reaching this state.
	 * </p>
	 */
	public static class Node {
		
		private int weapons;
		private int shots;
		
		/**
		 * Creates a node for a given accumulated cost and set of defeated bosses.
		 *
		 * @param shots   the total number of shots spent to reach this state
		 * @param weapons the bitmask of bosses already defeated (weapons owned)
		 */
		public Node(int shots, int weapons) {
			this.weapons = weapons;
			this.shots = shots;
		}
		/**
		 * @return the bitmask of bosses already defeated (weapons owned)
		 */
		public int getWeapons() {
			return weapons;
		}
		/**
		 * @param weapons the bitmask of bosses already defeated (weapons owned)
		 */
		public void setWeapons(int weapons) {
			this.weapons = weapons;
		}
		/**
		 * @return the total number of shots spent to reach this state
		 */
		public int getShots() {
			return shots;
		}
		/**
		 * @param shots the total number of shots spent to reach this state
		 */
		public void setShots(int shots) {
			this.shots = shots;
		}
		@Override
		public int hashCode() {
			return shots * 7 + weapons;
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			if (!getClass().equals(obj.getClass())) return false;
			Node that = (Node) obj;
			return this.weapons == that.weapons
					&& this.shots == that.shots;
		}
		@Override
		public String toString() {
			return "[weapons = " + weapons
					+ ", shots = " + shots
					+ "]";
		}
		
	}
}

