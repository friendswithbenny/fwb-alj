package org.fwb.alj.col;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class ListUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private ListUtil() { }
	
	public static <T> List<List<T>> asList1Deep(T[][] array) {
		return Lists.transform(
				Arrays.asList(array),
				new AsList<T>());
	}
	
	public static class AsList<T> implements Function<T[], List<T>> {
		@Override
		public List<T> apply(T[] input) {
			return Arrays.asList(input);
		}
	}
}
