package org.fwb.collection;

import java.util.Iterator;

public class IterableUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private IterableUtil() { }
	
	public static class IteratorIterable<T> implements Iterable<T> {
		final Iterator<T> I;
		public IteratorIterable(Iterator<T> i) {
			I = i;
		}
		@Override
		public Iterator<T> iterator() {
			return I;
		}
	}
}
