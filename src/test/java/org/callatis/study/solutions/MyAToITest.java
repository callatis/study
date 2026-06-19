package org.callatis.study.solutions;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class MyAToITest {

    private MyAToI parser;

    @Before
    public void setUp() {
        parser = new MyAToI();
    }

    @Test
    public void shouldParseExample1() {
        assertEquals(42, parser.myAtoi("42"));
    }

    @Test
    public void shouldParseExample2() {
        assertEquals(-42, parser.myAtoi(" -042"));
    }

    @Test
    public void shouldParseExample3() {
        assertEquals(1337, parser.myAtoi("1337c0d3"));
    }

    @Test
    public void shouldParseExample4() {
        assertEquals(0, parser.myAtoi("0-1"));
    }

    @Test
    public void shouldParseExample5() {
        assertEquals(0, parser.myAtoi("words and 987"));
    }

    @Test
    public void shouldClampBelowIntMin() {
        assertEquals(-2147483648, parser.myAtoi("-91283472332"));
    }

    @Test
    public void shouldReturnZeroForEmptyString() {
        assertEquals(0, parser.myAtoi(""));
    }

    @Test
    public void shouldReturnZeroForSingleSpace() {
        assertEquals(0, parser.myAtoi(" "));
    }

    @Test
    public void shouldReturnZeroForTwoSpaces() {
        assertEquals(0, parser.myAtoi("  "));
    }
}
