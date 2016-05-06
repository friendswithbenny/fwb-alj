package org.fwb.alj.func;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * utilities for converting between {@link Function} and {@link Predicate}.
 */
public class PredicateUtil {
	public static class PredicateFunction<T> implements Function<T, Boolean> {
		final Predicate<T> PREDICATE;
		public PredicateFunction(Predicate<T> predicate) {
			PREDICATE = predicate;
		}
		@Override
		public Boolean apply(T input) {
			return PREDICATE.apply(input);
		}
	}
	public static class FunctionPredicate<T> implements Predicate<T> {
		final Function<T, Boolean> FUNCTION;
		public FunctionPredicate(Function<T, Boolean> function) {
			FUNCTION = function;
		}
		@Override
		public boolean apply(T input) {
			return FUNCTION.apply(input);
		}
	}
}
