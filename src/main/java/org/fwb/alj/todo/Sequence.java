package org.fwb.alj.todo;

import java.util.Iterator;

/**
 * TODO finish
 * 
 * analogous to {@link Iterator} with two important and related distinctions:
 * (1) instead of (like Iterator and even Enumeration)
 * having two distinct methods to check availability and then retrieve,
 * this interface has only a single method to retrieve,
 * and its contract is to return null iff exhausted.
 * (2) according to that contract,
 * this Sequence cannot contain Null elements
 * 
 * a major advantage of this approach is synchronized access is possible at the api-level,
 * which is to say structures can be thread-safe by simply synchronizing the {@link #getNext()} method.
 * 
 * TODO some converters, which presumably need to store a one-item cache (converting in EITHER direction).
 * note at least one converter should gracefully skip nulls.
 */
interface Sequence<T> {
	/**
	 * 
	 * @return
	 */
	T getNext();
	
	interface Sequenceable<T> {
		Sequence<T> getSequence();
	}
	
	/** ignores null elements */
	class IteratorSequence<T> implements Sequence<T> {
		final Iterator<T> I;
		public IteratorSequence(Iterator<T> i) {
			I = i;
		}
		
		@Override
		public T getNext() {
			for (T retVal; I.hasNext(); )
				if (null != (retVal = I.next()))
					return retVal;
			return null;
		}
	}
}
