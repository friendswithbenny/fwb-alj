package org.fwb.alj.col;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * returns (an immutable, live-view of) the concatenation of a List of Lists.
 * the {@link #get(int)} and {@link size()} operations are O(m)
 * where m is the number of Lists being concatenated,
 * but the {@link Iterator#next()} method is O(1).
 */
public class ConcatList<T> extends AbstractList<T> {
	final List<? extends List<? extends T>> LISTS;
	public ConcatList(List<? extends List<? extends T>> lists) {
		LISTS = Preconditions.checkNotNull(lists);
	}
	
	@Override
	public T get(int index) {
		if (0 > index)
			throw new IndexOutOfBoundsException(
					String.format("invalid negative index %s", index));
		
		int adjustedIndex = index;
		
		for (List<? extends T> l : LISTS) {
			int size = l.size();
			if (adjustedIndex >= size)
				adjustedIndex -= size;
			else
				return l.get(adjustedIndex);
		}
		
		throw new IndexOutOfBoundsException(
				String.format("invalid index %s for size %s", index, size()));
	}
	
	@Override
	public int size() {
		return sumInts(Lists.transform(LISTS, SIZE));
	}
	
	/*
	 * a performance-enhanced over-ride which directly concatenates the iterators,
	 * rather than AbstractList's iterator which will repeatedly call the O(m) #get method.
	 */
	@Override
	public Iterator<T> iterator() {
		return Iterables.concat(LISTS).iterator();
	}
	
	static final Function<Collection<?>, Integer> SIZE =
			new Function<Collection<?>, Integer>() {
				@Override
				public Integer apply(Collection<?> input) {
					return input.size();
				}
			};
	
	static final int sumInts(List<Integer> ints) {
		int retVal = 0;
		for (int i : ints)
			retVal += i;
		return retVal;
	}
}