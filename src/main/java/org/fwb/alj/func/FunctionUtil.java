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
	
	// TODO UnaryConstructorFunction, NullaryInstanceMethodFunction, UnaryStaticMethodFunction
}
