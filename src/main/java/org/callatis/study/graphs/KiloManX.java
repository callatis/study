package org.callatis.study.graphs;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Solution for the <a href="https://community.topcoder.com/stat?c=problem_statement&rd=4725&pm=2288">KiloManX</a> 
 * TopCoder problem.
 * 
 * @author mpoplacenel
 */
public class KiloManX {
	
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
		SortedSet<Node> nodeSet = new TreeSet<Node>(new NodeComparator());
		for (int i = 0; i < n; i++) {
			nodeSet.add(new Node(bossHealth[i], 1 << i));
		}
		boolean[] visited = new boolean[(int) Math.pow(2, n)];
		visited[0] = true;
		while (nodeSet.size() > 0) {
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
	
	public static class NodeComparator implements Comparator<Node> {

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

	public static class Node {
		
		private int weapons;
		private int shots;
		
		public Node(int shots, int weapons) {
			this.weapons = weapons;
			this.shots = shots;
		}
		public int getWeapons() {
			return weapons;
		}
		public void setWeapons(int weapons) {
			this.weapons = weapons;
		}
		public int getShots() {
			return shots;
		}
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

