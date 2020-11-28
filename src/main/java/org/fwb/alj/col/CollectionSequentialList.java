package org.fwb.alj.col;

import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * 
 * (surprisingly this particular thin implementation exists in neither Java nor Guava.)
 */
public class CollectionSequentialList<T> extends AbstractSequentialList<T> {
	static final Logger LOG = LoggerFactory.getLogger(CollectionSequentialList.class);
	
	public static <T> List<T> asList(Collection<T> c) {
		return new CollectionSequentialList<T>(c);
	}
	
	private final Collection<T> C;
	private CollectionSequentialList(Collection<T> c) {
		C = c;
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new SequentialListIterator();
	}

	@Override
	public int size() {
		return C.size();
	}
	
	/** N.B. this implementation is NOT synchronized */
	class SequentialListIterator implements ListIterator<T> {
		private int nextIndex;
		private Iterator<T> i;
		private SequentialListIterator() {
			init(0);
		}
		/**
		 * N.B. this operation O(nextIndex) ~= O(|Collection|)
		 */
		private void init(int nextIndex) {
			Preconditions.checkArgument(0 <= nextIndex, "init(%s/%s)", nextIndex, size());
			// N.B.: this permits initialization of an already-exhausted iterator
			Preconditions.checkArgument(size() >= nextIndex, "init(%s/%s)", nextIndex, size());
			
			i = C.iterator();
			for (this.nextIndex = 0; this.nextIndex < nextIndex; )
				next();
		}
		
		@Override
		public boolean hasNext() {
			return size() > nextIndex();
		}
		@Override
		public T next() {
			T retVal = i.next();
			++ nextIndex;
			return retVal;
		}
		@Override
		public int nextIndex() {
			return nextIndex;
		}
		
		@Override
		public void remove() {
			i.remove();
			-- nextIndex;
		}
		
		@Override
		public boolean hasPrevious() {
			return 0 <= previousIndex();
		}
		/** N.B. this operation ~O(|Collection|) */
		@Override
		public T previous() {
			LOG.warn(
				"this method should never be necessary, and is very inefficiently implemented",
				new UnsupportedOperationException());
			init(previousIndex());
			return next();
		}
		@Override
		public int previousIndex() {
			return nextIndex() - 1;
		}
		
		/** @Deprecated this method only succeeds on already-exhausted iterators */
		@Override
		public void set(T e) {
			Preconditions.checkArgument(! hasNext(), "set(%s, %s)", nextIndex(), e);
			remove();
			add(e);
		}
		
		/** @Deprecated this method only succeeds on already-exhausted iterators */
		@Deprecated
		@Override
		public void add(T e) {
			Preconditions.checkArgument(! hasNext(), "add(%s, %s)", nextIndex(), e);
			CollectionSequentialList.this.add(e);
			++ nextIndex;
		}
	}
}
