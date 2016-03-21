package org.fwb.alj.col;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Preconditions;

public class CollectionUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private CollectionUtil() { }
	
	/**
	 * an alternative which returns slightly more granular info than boolean pass/fail.
	 * @return the number of add operations which "succeed" (modify the collection)
	 * @see Collection#addAll(Collection)
	 */
	public static <E> int addAll(Collection<E> collection, Iterator<? extends E> add) {
		int retVal = 0;
		while (add.hasNext())
			if (collection.add(add.next()))
				++ retVal;
		return retVal;
	}
	
	/**
	 * @throws IllegalArgumentException if an add operation "soft fails" (does not modify the collection)
	 * @see Collection#addAll(Collection)
	 */
	public static <E> void addAllForce(Collection<E> collection, Iterator<? extends E> add) {
		while (add.hasNext()) {
			E next = add.next();
			Preconditions.checkArgument(collection.add(next),
					"collection did not accept next element %s: %s", next, collection);
		}
	}
}
