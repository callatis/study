package org.callatis.study.utils;

/**
 * @author mpoplacenel
 * This class models a homogenous pair, i.e. both her items are of the same type.
 * For heterogeneous, check {@link AuPair}.
 *
 * @param <T> type of both pair elements.
 */
public class Pair<T> extends AuPair<T, T> {

    public Pair(T x, T y) {
        super(x, y);
    }

    public Pair() {
        super();
    }

}