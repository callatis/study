package org.callatis.study.utils;

/**
 * @author mpoplacenel
 * This class models a heterogeneous pair. Its name comes from the term "au-pair", which
 * actually has nothing to do with "pair" but sounds fun. Since this is not Production code,
 * I can afford to err on the fun rather than the maintainable :). 
 *
 * @param <T> type of first element
 * @param <U> type of second element
 */
public class AuPair<T, U> {
	
	public T x;
	public U y;
	
	public AuPair() {
		super();
	}

	public AuPair(T x, U y) {
		super();
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
}