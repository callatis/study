package org.study.callatis.utils;

import org.callatis.study.utils.BitFactoryCounting;
import org.junit.Assert;
import org.junit.Test;

public class BitFactoryCountingTest {
	
	private BitFactoryCounting factory = new BitFactoryCounting();

	@Test
	public void test112() throws Exception {
		final String[] county = new String[] { "NY", "YN" };
		System.out.println("(1, 1, 2)=" + this.factory.combinations(1, 1, county, 2));
		Assert.assertEquals(2, this.factory.count(1, 1, county));
	}

	@Test
	public void test114() throws Exception {
		final String[] county = new String[] { "NYYY", "YNYY", "YYNY", "YYYN" };
		System.out.println("(1, 1, 4)=" + this.factory.combinations(1, 1, county, 4));
		Assert.assertEquals(12, this.factory.count(1, 1, county));
	}

	@Test
	public void test125() throws Exception {
		final String[] county = new String[] { "NYYYY", "YNYYN", "YYNYY", "YYYNY", "YNYYN" };
		System.out.println("(1, 2, 5)=" + this.factory.combinations(1, 2, county, 5));
		Assert.assertEquals(24, this.factory.count(1, 2, county));
	}

	@Test
	public void test213() throws Exception {
		final String[] county = new String[] { "NYY", "YNY", "YYN" };
		System.out.println("(2, 1, 3)=" + this.factory.combinations(2, 1, county, 3));
		Assert.assertEquals(3, this.factory.count(2, 1, county));
	}

	@Test
	public void test226() throws Exception {
		final String[] county = new String[] { "NYYYYN", "YNYYNY", "YYNYYY", "YYYNYN", "YNYYNY", "NYYNYN" };
		System.out.println("(2, 2, 6)=" + this.factory.combinations(2, 2, county, 6));
		Assert.assertEquals(32, this.factory.count(2, 2, county));
	}

}
