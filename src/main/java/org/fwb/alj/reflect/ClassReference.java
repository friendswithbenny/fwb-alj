package org.fwb.alj.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * this experimental utility class should be used with caution.
 * its design still needs review,
 * and its implementation is non-optimal (instance-inner classes).
 */
public class ClassReference<T> {
	public final Class<T> CLS;
	
	public ClassReference(Class<T> cls) {
		CLS = cls;
	}
	
	/**
	 * @see Class#getMethod(String, Class...)
	 * @throws IllegalArgumentException if the return type of the method specified is not equal to {@code returnType}
	 */
	public <R> Method getPublicMethod(Class<R> returnType, String methodName, Class<?>... args)
			throws NoSuchMethodException {
		Method retVal = CLS.getMethod(methodName, args);
		// TODO probably needs to allow sub-classes?
		Preconditions.checkArgument(returnType == retVal.getReturnType());
		return retVal;
	}
	/**
	 * @see Class#getDeclaredMethod(String, Class...)
	 * @throws IllegalArgumentException if the return type of the method specified is not equal to {@code returnType}
	 */
	public <R> Method getDeclaredMethod(Class<R> returnType, String methodName, Class<?>... args)
			throws NoSuchMethodException {
		Method retVal = CLS.getDeclaredMethod(methodName, args);
		// TODO probably needs to allow sub-classes?
		Preconditions.checkArgument(returnType == retVal.getReturnType());
		return retVal;
	}
	
	public class UnaryConstructorFunction<F> implements Function<F, T> {
		final Constructor<T> CTOR;
		public UnaryConstructorFunction(Class<F> fromClass) throws NoSuchMethodException, SecurityException {
			CTOR = CLS.getDeclaredConstructor(fromClass);
		}
		
		@Override
		public T apply(F input) {
			return ReflectionUtil.newInstanceUnchecked(CTOR, input);
		}
	}
	
	class InnerClassReference<EI> {
		final EI ENCLOSING_INSTANCE;
		InnerClassReference(EI enclosingInstance) {
			ENCLOSING_INSTANCE = enclosingInstance;
		}
		
		class InnerUnaryConstructorFunction<F> implements Function<F, T> {
			final Constructor<T> CTOR;
			InnerUnaryConstructorFunction(Class<? super F> fromClass) {
				try {
					CTOR = CLS.getDeclaredConstructor(ENCLOSING_INSTANCE.getClass(), fromClass);
				} catch (NoSuchMethodException e) {
					for (Constructor<?> ctor : CLS.getDeclaredConstructors()) {
						Class<?>[] argtypes = ctor.getParameterTypes();
						Preconditions.checkArgument(
								ENCLOSING_INSTANCE.getClass().equals(argtypes[0]),
								"%s != %s", ENCLOSING_INSTANCE.getClass(), argtypes[0]);
						Preconditions.checkArgument(
								fromClass.equals(argtypes[1]),
								"%s != %s", fromClass, argtypes[1]);
					}
					throw new RuntimeException(e);
				} catch (SecurityException e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public T apply(F input) {
				return ReflectionUtil.newInstanceUnchecked(CTOR, ENCLOSING_INSTANCE, input);
			}
		}
	}
	
	/** map from an instance to a nullary instance-method's return value */
	class NullaryMethodFunction<R> implements Function<T, R> {
		final Method M;
		NullaryMethodFunction(Class<R> returnType, String methodName) throws NoSuchMethodException {
			M = getDeclaredMethod(returnType, methodName);
		}
		
		@Override
		public R apply(T input) {
			@SuppressWarnings("unchecked")
			R r = (R) ReflectionUtil.invokeUnchecked(input, M);
			return r;
		}
	}
	
	public final InstanceReference STATIC = new InstanceReference();
	
	/** reflection-facilitated reference to an instance (or null, for {@code static}) */
	public class InstanceReference {
		final T I;
		public InstanceReference(T i) {
			I = Preconditions.checkNotNull(i);
		}
		
		/** special singleton constructor to symbolize {@code static} */
		private InstanceReference() {
			I = null;
		}
		
		boolean isStatic() {
			return null == I;
		}
		
		public class UnaryMethodFunction<K, V> implements Function<K, V> {
			final Method M;
			
			public UnaryMethodFunction(Class<V> returnType, String methodName, Class<K> argType) throws NoSuchMethodException {
				M = (getDeclaredMethod(returnType, methodName, argType));
				
				Preconditions.checkArgument(
						isStatic() == Modifier.isStatic(M.getModifiers()),
						"static InstanceReference can't have non-static method: %s", M);
			}
			
			@Override
			public V apply(K input) {
				@SuppressWarnings("unchecked")
				V v = (V) ReflectionUtil.invokeUnchecked(input, M);
				return v;
			}
		}
	}
}
