package org.fwb.collection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingList;

/**
 * these utilities are specifically related to Sets of (distinct) elements.
 */
public class Sets2 {
	/** @deprecated static utilities only */
	@Deprecated
	private Sets2() { }
	
	/** a List whose elements are unique */
	public static interface SetAndList<T> extends Set<T>, List<T> { }
	
	/** @return true iff {@code c} is an instance of BOTH {@link Set} and {@link List} */
	public static boolean isSetAndList(Collection<?> c) {
		return c instanceof Set && c instanceof List;
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
		/** @param c results are undefined if this does not adhere to {@link Set} interface. */
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
			 * but it requires the input already be an instance of Set (as well as List),
			 * and it returns any instance of SetAndList directly without wrapping it.
			 * 
			 * @throws NullPointerException if {@code c} is null
			 * @throws IllegalArgumentException if {@code c} is not an instance of BOTH Set and List
			 */
			public static <T> SetAndList<T> asSetAndList(Collection<T> c) {
				if (c instanceof SetAndList)
					return (SetAndList<T>) c;
				
				Preconditions.checkNotNull(c,
						"not coercible to SetAndList: null");
				
				Preconditions.checkArgument(isSetAndList(c),
						"not coercible to SetAndList: %s",
						c.getClass());
				
				return new ListSetView<T>((List<T>) c);
			}
			
			final List<T> L;
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
