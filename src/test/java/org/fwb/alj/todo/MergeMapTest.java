package org.fwb.alj.todo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertEquals;

/** @deprecated TODO more tests */
public class MergeMapTest {
	static final Object OBJECT = new Object();
	
	@Test
	public void test() {
		Map<Object, Object> m = new LinkedHashMap<Object, Object>();
		m.put("foo", 13);
		m.put(13, ImmutableList.of(
				"abc",
				123));
		MergeMap.deepMerge(m,
				Collections.singletonMap("bar", 123.45));
		assertEquals(
				ImmutableMap.of(
						"foo", 13,
						"bar", 123.45,
						13, ImmutableList.of(
								"abc",
								123)),
				m);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCollision() {
		Map<Object, Object> m = new LinkedHashMap<Object, Object>();
		m.put("foo", 13);
		MergeMap.deepMerge(
				m,
				Collections.singletonMap("foo", 14));
	}
}
