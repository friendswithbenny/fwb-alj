package org.fwb.alj.col;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

/**
 * returns (an immutable, live-view of) the concatenation of a List of Lists.
 * the {@link #get(int)} and {@link size()} operations are O(m)
 * where m is the number of Lists being concatenated,
 * but the {@link Iterator#next()} method is O(1).
 */
public class ConcatCollection<T> extends AbstractCollection<T> {
	final Iterable<? extends Collection<? extends T>> COLLECTIONS;
	public ConcatCollection(Iterable<? extends Collection<? extends T>> lists) {
		COLLECTIONS = Preconditions.checkNotNull(lists);
	}
	
	@Override
	public int size() {
		return sumInts(
				Iterables.transform(COLLECTIONS, SIZE));
	}
	
	@Override
	public Iterator<T> iterator() {
		return Iterables.concat(COLLECTIONS).iterator();
	}
	
	
	static final Function<Collection<?>, Integer> SIZE =
			new Function<Collection<?>, Integer>() {
				@Override
				public Integer apply(Collection<?> input) {
					return input.size();
				}
			};
	
	static final int sumInts(Iterable<Integer> ints) {
		int retVal = 0;
		for (int i : ints)
			retVal += i;
		return retVal;
	}
}