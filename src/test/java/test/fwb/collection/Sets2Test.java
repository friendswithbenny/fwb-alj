package test.fwb.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.fwb.collection.Sets2;
import org.fwb.collection.Sets2.NonDistinctException;
import org.fwb.collection.Sets2.SetAndList;
import org.fwb.collection.Sets2.SetView;
import org.fwb.collection.Sets2.SetView.ListSetView;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class Sets2Test {
	static final Logger LOG = LoggerFactory.getLogger(Sets2Test.class);
	
	@Test
	public void testIsSetAndList() {
		assertFalse(Sets2.isSetAndList(new HashSet<Object>()));
		assertFalse(Sets2.isSetAndList(new ArrayList<Object>()));
		assertFalse(Sets2.isSetAndList(new LinkedList<Object>()));
		assertTrue(Sets2.isSetAndList(new CheatSetAndList<Object>()));
		
		// some interesting but spec-undefined variables:
		LOG.info("Collections.emptySet() isSetAndList? {}",
				Sets2.isSetAndList(Collections.emptySet()));
		LOG.info("Collections.emptyList() isSetAndList? {}",
				Sets2.isSetAndList(Collections.emptyList()));
		LOG.info("Collections.singleton(_) isSetAndList? {}",
				Sets2.isSetAndList(Collections.singleton("foo")));
		LOG.info("Collections.singletonList(_) isSetAndList? {}",
				Sets2.isSetAndList(Collections.singletonList("bar")));
	}
	
	@Test
	public void testSetView() {
		assertTrue(new SetView<Object>(new ArrayList<Object>()) instanceof Set);
	}
	@Test
	public void testListSetView() {
		assertTrue(Sets2.isSetAndList(new ListSetView<Object>(new ArrayList<Object>())));
	}
	
	@Test
	public void testAsSetAndList_Same() {
		SetAndList<?> x = new ListSetView<Object>(new ArrayList<Object>());
		assertSame(x, ListSetView.asSetAndList(x));
	}
	@Test
	public void testAsSetAndList_Pass() {
		Collection<?>
			x = new CheatSetAndList<Object>(),
			y = ListSetView.asSetAndList((List<?>) x);
		
		assertTrue(Sets2.isSetAndList(x));
		assertFalse(x instanceof SetAndList);
		assertNotSame(x, y);
		assertTrue(y instanceof SetAndList);
		assertTrue(Sets2.isSetAndList(y));
	}
	@Test
	public void testAsSetAndList_Fail() {
		try {
			ListSetView.asSetAndList(new ArrayList<Object>());
			fail();
		} catch (IllegalArgumentException e) {
			return; // pass
		}
	}
	
	@Test
	public void testCheckUnique_Pass() {
		NonDistinctException.checkUnique(Arrays.asList(
				'a', 3, "BB", new Object(), null));
	}
	@Test
	public void testCheckUnique_Fail() {
		// finds duplicate nulls
		try {
			NonDistinctException.checkUnique(Arrays.asList(
					'a', 3, null, new Object(), null));
			fail();
		} catch (NonDistinctException e) {
			return; // pass
		}
		
		// finds duplicate non-nulls (using Iterator)
		try {
			NonDistinctException.checkUnique(Arrays.asList(
					'a', 3, "BB", new Object(), 3).iterator());
			fail();
		} catch (NonDistinctException e) {
			return; // pass
		}
	}
	
	/** a {@link Set} AND {@link List}, but n.b. does NOT implement {@link SetAndList} */
	@SuppressWarnings("serial")
	static class CheatSetAndList<T> extends ArrayList<T> implements Set<T> { }
}
