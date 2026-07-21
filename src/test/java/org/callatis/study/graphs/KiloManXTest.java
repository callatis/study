package org.callatis.study.graphs;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Tests for {@link KiloManX#leastShots(String[], int[])} covering the official TopCoder KiloManX
 * examples.
 *
 * <p>
 * Each case supplies a square {@code damageChart} of per-shot damage digits together with the
 * matching {@code bossHealth} array, and asserts the minimum total number of shots needed to defeat
 * every boss. The cases range from small charts to the maximum {@code 15}-boss example, exercising
 * the default-weapon fallback and the Dijkstra ordering over the {@code 2^n} states.
 * </p>
 */
public class KiloManXTest {
	
	/**
	 * Three bosses where each row's weapon damages exactly one other boss; the optimal ordering
	 * totals {@code 218} shots.
	 */
	@Test
	public void testLeastShots0() {
		String[] damageChart = { "070","500","140" };
		int[] bossHealth = { 150, 150, 150 };
		assertEquals(218, KiloManX.leastShots(damageChart, bossHealth));
	}

	/**
	 * Four bosses with mutually useful weapons; the optimal ordering totals {@code 205} shots.
	 */
	@Test
	public void testLeastShots1() {
		assertEquals(205, KiloManX.leastShots(new String[] {"1542", "7935", "1139", "8882"}, 
				new int[] {150,150,150,150}));
	}
	
	/**
	 * Two bosses where killing the weaker boss first unlocks a strong weapon against the other;
	 * the optimal total is {@code 48} shots.
	 */
	@Test
	public void testLeastShots2() {
		assertEquals(48, KiloManX.leastShots(new String[] {"07", "40"}, 
				new int[] {150,10}));
	}
	
	/**
	 * The maximum-size example: {@code 15} bosses with widely varying health, exercising the full
	 * {@code 2^15}-state search; the optimal total is {@code 260445} shots.
	 */
	@Test
	public void testLeastShots3() {
		assertEquals(260445, KiloManX.leastShots(new String[] {"198573618294842",
				 "159819849819205",
				 "698849290010992",
				 "000000000000000",
				 "139581938009384",
				 "158919111891911",
				 "182731827381787",
				 "135788359198718",
				 "187587819218927",
				 "185783759199192",
				 "857819038188122",
				 "897387187472737",
				 "159938981818247",
				 "128974182773177",
				 "135885818282838"}, 
				new int[] {157, 1984, 577, 3001, 2003, 2984, 5988, 190003,
						9000, 102930, 5938, 1000000, 1000000, 5892, 38}));
	}
	
	/**
	 * Eight bosses where every weapon deals at least {@code 1} damage everywhere, so ordering only
	 * shaves off shots via the stronger diagonal-adjacent weapons; the optimal total is {@code 92}
	 * shots.
	 */
	@Test
	public void testLeastShots4() {
		assertEquals(92, KiloManX.leastShots(new String[] 
				{"02111111", "10711111", "11071111", "11104111",
				 "41110111", "11111031", "11111107", "11111210"}, 
				new int[] {28,28,28,28,28,28,28,28}));
	}
	
}
