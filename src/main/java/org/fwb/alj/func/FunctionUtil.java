package org.fwb.alj.func;

import java.util.concurrent.Callable;

import com.google.common.base.Function;

public class FunctionUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private FunctionUtil() {
	}
	
	public static class CallCallable<T> implements Function<Callable<T>, T> {
		@Override
		public T apply(Callable<T> input) {
			try {
				return input.call();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	// TODO UnaryConstructorFunction, NullaryInstanceMethodFunction, UnaryStaticMethodFunction
}
