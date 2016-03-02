package org.fwb.collection;

import java.util.Iterator;

import com.google.common.base.Preconditions;

public class IteratorUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private IteratorUtil() {
	}
	
	/**
	 * source: {@link AbstractCollection#toString()}
	 * @param i not null
	 */
	public static String toString(Iterator<?> i) {
		Preconditions.checkNotNull(i);
		
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		while (i.hasNext()) {
			sb.append(i.next());
			
			if (i.hasNext())
				sb.append(", ");
		}
		return sb.append(']').toString();
	}
	
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
