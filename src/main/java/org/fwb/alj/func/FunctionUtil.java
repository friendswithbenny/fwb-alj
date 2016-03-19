package org.fwb.alj.func;

import java.util.concurrent.Callable;

import com.google.common.base.Function;

public class FunctionUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private FunctionUtil() {
	}
	
	public static class CallCallable<C extends Callable<V>, V> implements Function<C, V> {
		@Override
		public V apply(C input) {
			try {
				return input.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static Function<Object, Class<?>> GET_CLASS =
			new Function<Object, Class<?>>() {
				@Override
				public Class<?> apply(Object input) {
					return input.getClass();
				}
			};
	
	/** @deprecated TODO consider this. is it safe? is it unsafe? */
	static class GetClass<T> implements Function<T, Class<T>> {
		@Override
		@SuppressWarnings("unchecked")
		public Class<T> apply(T input) {
			return (Class<T>) input.getClass();
		}
	}
	
	// TODO UnaryConstructorFunction, NullaryInstanceMethodFunction, UnaryStaticMethodFunction
}
