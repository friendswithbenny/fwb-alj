package org.fwb.alj.col;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;

/**
 * returns (an immutable, live-view of) the concatenation of a List of Lists.
 * the {@link #get(int)} and {@link size()} operations are O(m)
 * where m is the number of Lists being concatenated,
 * but the {@link Iterator#next()} method is O(1).
 * 
 * n.b. it was easier to extend AbstractList and repeat some of {@link ConcatCollection}'s implementation,
 * than to extend ConcatCollection and need to implement all the special {@link List} methods from-scratch.
 */
public class ConcatList<T> extends AbstractList<T> {
	final Iterable<? extends List<? extends T>> LISTS;
	public ConcatList(Iterable<? extends List<? extends T>> lists) {
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
		return ConcatCollection.sumInts(
				Iterables.transform(LISTS, ConcatCollection.SIZE));
	}
	
	/*
	 * a performance-enhanced over-ride which directly concatenates the iterators,
	 * rather than AbstractList's iterator which will repeatedly call the O(m) #get method.
	 */
	@Override
	public Iterator<T> iterator() {
		return Iterables.concat(LISTS).iterator();
	}
}