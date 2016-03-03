package test.fwb.alj.col;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.fwb.alj.col.ListIndex;
import org.fwb.alj.col.SetUtil.SetView.ListSetView;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class ListIndexTest {
	static final String NAME = "A first item";
	
	static final List<?> INPUT_UNIQUE = Arrays.asList(
			NAME,
			new Object(),
			3);
	static final Map<Object, Integer> EXPECTED_UNIQUE = ImmutableMap.of(
			NAME, 0,
			INPUT_UNIQUE.get(1), 1,
			3, 2);
	
	static final Collection<?> INPUT_DUPE = Arrays.asList(
			NAME,
			new Object(),
			NAME);
	static final Map<Object, Integer> EXPECTED_DUPE = ImmutableMap.of(
			NAME, 0,
			Iterators.get(INPUT_DUPE.iterator(), 1), 1);
	
	@Test
	public void testGetListIndex_CollectionUnique() {
		assertEquals(EXPECTED_UNIQUE,
				ListIndex.getListIndex(INPUT_UNIQUE));
	}
	@Test
	public void testGetListIndex_IteratorDupe() {
		assertEquals(EXPECTED_DUPE,
				ListIndex.getListIndex(INPUT_DUPE.iterator()));
	}
	
	@Test
	public void testGetUniqueListIndex_Pass() {
		assertEquals(EXPECTED_UNIQUE,
				ListIndex.getUniqueListIndex(INPUT_UNIQUE.iterator()));
	}
	@Test
	public void testGetUniqueListIndex_Fail() {
		try {
			ListIndex.getUniqueListIndex(INPUT_DUPE.iterator());
			fail();
		} catch (IllegalArgumentException e) {
			return; // pass
		}
	}
	
	@Test
	public void testUniqueListIndexAlternative() {
		assertEquals(EXPECTED_UNIQUE,
				ListIndex.getUniqueListIndexAlternative(INPUT_UNIQUE));
		
		try {
			ListIndex.getUniqueListIndexAlternative((List<?>) INPUT_DUPE);
			fail();
		} catch (IllegalArgumentException e) {
			return; // pass
		}
	}
	
	@Test
	public void testGetLiveIndex() {
		assertEquals(EXPECTED_UNIQUE,
				ListIndex.getLiveIndex(
						newListSetView(INPUT_UNIQUE)));
//		assertEquals(EXPECTED_DUPE,
//				ListIndex.getLiveIndex(
//						// I am not a monster; we don't coerce non-distinct collections into Sets
//						new ListSetView<>((List<?>) INPUT_DUPE)));
	}
	/** an example of why we need Java7's "diamond" operator */
	<T> ListSetView<T> newListSetView(List<T> l) {
		return new ListSetView<T>(l);
	}
	
	@Test
	public void testAsMap() {
		Map<Integer, ?> expectedUnique = ImmutableMap.of(
				0, NAME,
				1, INPUT_UNIQUE.get(1),
				2, 3);
		Map<Integer, ?> expectedDupe = ImmutableMap.of(
				0, NAME,
				1, Iterators.get(INPUT_DUPE.iterator(), 1),
				2, NAME);
		
		assertEquals(expectedUnique,
				ListIndex.asMap(INPUT_UNIQUE));
		assertEquals(expectedDupe,
				ListIndex.asMap((List<?>) INPUT_DUPE));
	}
}
