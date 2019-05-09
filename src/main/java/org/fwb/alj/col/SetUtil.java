package org.fwb.alj.col;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Spliterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingList;

/**
 * these utilities are specifically related to Sets of (distinct) elements.
 * TODO extract some of these inner classes?
 */
public class SetUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private SetUtil() { }
	
	/** a List whose elements are unique */
	public static interface SetAndList<T> extends Set<T>, List<T> {
		Spliterator<T> spliterator();
	}
	
	/** @return true iff {@code o} is an instance of BOTH {@link Set} and {@link List} */
	public static boolean isSetAndList(Object o) {
		return o instanceof Set && o instanceof List;
	}
	
	/** @return true iff {@code list} is an instance of {@link Set} */
	public static boolean isSet(List<?> list) {
		return list instanceof Set;
	}
	
	/** @return true iff {@code set} is an instance of {@link List} */
	public static boolean isList(Set<?> set) {
		return set instanceof List;
	}
	
	/**
	 * from time to time, one may receive a reference to a Collection which is NOT an instanceof Set.
	 * yet business rules or computational logic dictate that it is, in fact, a Set by mathematical/logical definition (conforms to the uniqueness constraint).
	 * this class may be used to compose a Set reference to that exact Collection in such cases.
	 * 
	 * N.B. great care must be taken to ONLY use this class in cases where uniqueness is guaranteed! behavior is otherwise undefined.
	 * N.B. this class does not address certain cases needing other subclasses of Collection (e.g. SortedSet or Set+List),
	 * but it serves as a simple example of how to procure such structures.
	 */
	public static class SetView<E>
			extends ForwardingCollection<E>
			implements Set<E> {
		
		final Collection<E> C;
		/**
		 * @param c needn't be an instance of {@link Set}, but must conform to all contracts of the Set interface.
		 */
		public SetView(Collection<E> c) {
			C = c;
		}
		
		public void checkUnique() throws NonDistinctException {
			NonDistinctException.checkUnique(this);
		}
		
		@Override
		protected Collection<E> delegate() {
			return C;
		}
		
		public static class ListSetView<T>
				extends ForwardingList<T>
				implements SetAndList<T> {
			
			/**
			 * this method is analogous to the constructor,
			 * but it requires the input already be an instance of the Set interface
			 * (and it returns any instance of SetAndList directly without wrapping it).
			 * 
			 * @param setAndList an instance of {@link Set} (as well as {@link List})
			 * @return an instance of SetAndList
			 * 
			 * @throws NullPointerException if {@code c} is null
			 * @throws IllegalArgumentException if {@code c} is not an instance of BOTH Set and List
			 */
			public static <T> SetAndList<T> asSetAndList(List<T> setAndList) {
				if (setAndList instanceof SetAndList)
					return (SetAndList<T>) setAndList;
				
				Preconditions.checkNotNull(setAndList,
						"not coercible to SetAndList: null");
				
				Preconditions.checkArgument(isSet(setAndList),
						"not coercible to SetAndList: %s",
						setAndList.getClass());
				
				return new ListSetView<T>(setAndList);
			}
			
			final List<T> L;
			/**
			 * @param l needn't be an instance of {@link Set}, but must conform to all contracts of the Set interface.
			 */
			public ListSetView(List<T> l) {
				L = l;
			}
			
			public void checkUnique() throws NonDistinctException {
				NonDistinctException.checkUnique(this);
			}
			
			@Override
			protected List<T> delegate() {
				return L;
			}

			@Override
			public Spliterator<T> spliterator() {
				return super.spliterator();
			}
		}
	}
	
	/** thrown when a {@link Collection} should contain distinct elements, but collisions are found */
	public static class NonDistinctException extends IllegalArgumentException {
		/** default */
		private static final long serialVersionUID = 1L;
		
		static final Logger LOG = LoggerFactory.getLogger(NonDistinctException.class);
		
		/** @see #checkUnique(Iterator) */
		public static void checkUnique(Iterable<?> i) throws NonDistinctException {
			checkUnique(i.iterator());
		}
		/** @throws NonDistinctException if {@code i} yields non-distinct elements */
		public static void checkUnique(Iterator<?> i) throws NonDistinctException {
			Collection<Object>
				test = new HashSet<Object>(),
				fail = new LinkedList<Object>();
			for (Object o; i.hasNext(); ) {
				o = i.next();
				if (! test.add(o))
					fail.add(o);
			}
			if (! fail.isEmpty())
				throw new NonDistinctException(test, fail);
		}
		
		final Collection<?>
				C,
				COLLISIONS;
		
		public NonDistinctException(Collection<?> shouldBeDistinct, Collection<?> collisions) {
			super(String.format("found non-distinct elements: %s", collisions));
			
			Preconditions.checkNotNull(shouldBeDistinct,
					"first argument to NonDistinctException mustn't be null");
			
			LOG.trace(
					String.format("found duplicates in (%s): %s",
							shouldBeDistinct.getClass(),
							shouldBeDistinct),
					this);
			
			C = shouldBeDistinct;
			COLLISIONS = collisions;
		}
	}
}
