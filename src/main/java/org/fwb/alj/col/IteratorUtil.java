package org.fwb.alj.col;

//import java.util.Enumeration;
import java.util.Iterator;

import com.google.common.base.Preconditions;

public class IteratorUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private IteratorUtil() { }
	
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
	
//	/**
//	 * @deprecated @see {@link com.google.common.collect.Iterators#forEnumeration(Enumeration)}
//	 */
//	public static class EnumerationIterator<T> implements Iterator<T> {
//		final Enumeration<T> E;
//		EnumerationIterator(Enumeration<T> e) {
//			E = e;
//		}
//		@Override
//		public boolean hasNext() {
//			return E.hasMoreElements();
//		}
//		@Override
//		public T next() {
//			return E.nextElement();
//		}
//	}
	
	public static class IteratorIterable<T> implements Iterable<T> {
		final Iterator<T> I;
		
		public IteratorIterable(Iterator<T> i) {
			I = i;
		}
		
		@Override
		public Iterator<T> iterator() {
			return I;
		}
		
		/**
		 * @deprecated TODO
		 * test!
		 * rename!
		 * track down and replace (SOME) references to IteratorIterable
		 * (presumably by deprecating it for a while?)
		 */
		public static class StrictIteratorIterable<T> extends IteratorIterable<T> {
			private Throwable
				create = null,
				firstCall = null;
			
			public StrictIteratorIterable(Iterator<T> i) {
				super(i);
				
				create = new Throwable("create");
			}
			
			@Override
			public Iterator<T> iterator() {
				if (null != firstCall)
					throw new IllegalStateException(
							"disallowed second #iterator call", firstCall);
				
				firstCall = new Throwable("first #iterator call", create);
				
				return super.iterator();
			}
		}
	}
}
