package org.fwb.alj.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private ReflectionUtil() {
	}
	
	public Method getPublicMethodUnchecked(Class<?> cls, String methodName, Class<?>... args) {
		try {
			return cls.getMethod(methodName, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	public Method getDeclaredMethodUnchecked(Class<?> cls, String methodName, Class<?>... args) {
		try {
			return cls.getDeclaredMethod(methodName, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @see Constructor#newInstance(Object...)
	 * @throws RuntimeException
	 */
	public static <T> T newInstanceUnchecked(Constructor<T> constructor, Object... args) {
		try {
			return newInstanceLessChecked(constructor, args);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	/**
	 * @see Constructor#newInstance(Object...)
	 * @throws RuntimeException
	 */
	public static <T> T newInstanceLessChecked(Constructor<T> constructor, Object... args) throws InvocationTargetException {
		try {
			return constructor.newInstance(args);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object invokeUnchecked(Object o, Method m, Object... args) {
		try {
			return invokeLessChecked(o, m, args);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	public static Object invokeLessChecked(Object o, Method m, Object... args) throws InvocationTargetException {
		try {
			return m.invoke(o, args);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
