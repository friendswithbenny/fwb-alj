package org.fwb.collection;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fwb.collection.ListIndex.Indices.CollectionIndices;
import org.fwb.collection.Sets2.SetAndList;

import com.google.common.base.Function;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * these utilities are specifically related to the integer index of {@link List} elements.
 */
public class ListIndex {
	/** @deprecated static utilities only */
	@Deprecated
	private ListIndex() { }
	
	/** @see #getListIndex(Iterator) */
	public static <T> ImmutableMap<T, Integer> getListIndex(Iterable<T> collection) {
		return getListIndex(collection.iterator());
	}
	
	/**
	 * returns a snapshot view of the collection as a reverse-index.
	 * duplicate keys are mapped to their first-occurring index.
	 * lookup operations are O(1) at the cost of this builder method being O(n).
	 */
	public static <T> ImmutableMap<T, Integer> getListIndex(Iterator<T> collection) {
		Builder<T, Integer> b = ImmutableMap.builder();
		for (int i = 0; collection.hasNext(); ++i) {
			T t = collection.next();
			
			// TODO sad I couldn't find an off-the-shelf guava way to implement this
			if (! b.build().containsKey(t))
				
				b.put(t, i);
		}
		return b.build();
	}
	
	/**
	 * this alternative approach fails on duplicate elements.
	 * @throws IllegalArgumentException if a duplicate element is encountered
	 */
	public static <T> ImmutableMap<T, Integer> getUniqueListIndex(Iterator<T> collection) {
		Builder<T, Integer> b = ImmutableMap.builder();
		for (int i = 0; collection.hasNext(); ++i)
			b.put(collection.next(), i);
		return b.build();	// IAE
	}
	/**
	 * this alternative approach uses {@link Maps#uniqueIndex} and fails on duplicate elements.
	 * @throws IllegalArgumentException if a duplicate element is encountered
	 */
	public static <T> ImmutableMap<T, Integer> getUniqueListIndexAlternative(List<T> list) {
		return Maps.uniqueIndex(new CollectionIndices(list), new AtIndex<T>(list));
	}
	
	/**
	 * returns a live view which delegates to {@link List#indexOf(Object)}.
	 * @see ListSetView
	 */
	public static <T> Map<T, Integer> getLiveIndex(SetAndList<T> list) {
		return Maps.asMap(list, new IndexOf<T>(list));
	}
	
	public static <T> Map<Integer, T> asMap(List<T> list) {
		return Maps.asMap(new CollectionIndices(list), new AtIndex<T>(list));
	}
	
	/**
	 * calls {@link List#get(int)}.
	 * this can be thought of as "Functions.forList(_)",
	 * an analog to {@link Functions#forMap(Map)}.
	 */
	static class AtIndex<T> implements Function<Integer, T> {
		final List<T> LIST;
		AtIndex(List<T> list) {
			LIST = list;
		}
		
		@Override
		public T apply(Integer input) {
			return LIST.get(input);
		}
	}
	
	/** calls {@link List#indexOf(Object)} */
	static class IndexOf<T> implements Function<T, Integer> {
		final List<T> LIST;
		IndexOf(List<T> list) {
			LIST = list;
		}
		
		@Override
		public Integer apply(T input) {
			return LIST.indexOf(input);
		}
	}
	
	/** the set/list of numbers from 0 (inclusive) to {@link #size()} (exclusive). */
	static abstract class Indices extends AbstractList<Integer> implements Set<Integer> {
		@Override
		public Integer get(int index) {
//			return Preconditions.checkElementIndex(index, size());
			return index;
		}
		
		/** constant implementation */
		static class FixedIndices extends Indices {
			final int SIZE;
			FixedIndices(int size) {
				SIZE = size;
			}
			@Override
			public int size() {
				return SIZE;
			}
		}
		/** pass-through implementation */
		static class CollectionIndices extends Indices {
			final Collection<?> C;
			CollectionIndices(Collection<?> c) {
				C = c;
			}
			@Override
			public int size() {
				return C.size();
			}
		}
	}
	
	/**
	 * Immutable (copy of a) List
	 * with O(1) {@link #indexOf(Object)} operation
	 * at the expense of (2x) O(n) constructor.
	 */
	public class IndexedList<T> extends AbstractList<T> {
		final ImmutableList<T> DELEGATE;
		final ImmutableMap<T, Integer> INDEX;
		
		/** O(n) */
		public IndexedList(Collection<T> delegate) {
			DELEGATE = ImmutableList.copyOf(delegate);
			INDEX = getListIndex(DELEGATE);
		}
		
		public ImmutableMap<T, Integer> getIndex() {
			return INDEX;
		}
		
		/** performance-enhanced: O(1) */
		@Override
		public int indexOf(Object o) {
			return MoreObjects.firstNonNull(INDEX.get(o), -1);
		}
		
		@Override
		public T get(int index) {
			return DELEGATE.get(index);
		}
	
		@Override
		public int size() {
			return DELEGATE.size();
		}
	}
}