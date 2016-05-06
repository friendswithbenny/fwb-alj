package org.fwb.alj.cls;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

public class ClassReference<T> {
	public final Class<T> CLS;
	
	public ClassReference(Class<T> cls) {
		CLS = cls;
	}
	
	/**
	 * @see Class#getDeclaredConstructor(Class...)
	 * @throws RuntimeException
	 */
	public Constructor<T> getConstructor(Class<?>... args) {
		try {
			return(CLS.getDeclaredConstructor(args));
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @see Class#getMethod(String, Class...)
	 * @throws IllegalArgumentException if the return type of the method specified is not equal to {@code returnType}
	 */
	public <R> Method getPublicMethod(Class<R> returnType, String methodName, Class<?>... args) {
		Method retVal = getPublicMethod(methodName, args);
		// TODO probably needs to allow sub-classes?
		Preconditions.checkArgument(returnType == retVal.getReturnType());
		return retVal;
	}
	/**
	 * @see Class#getDeclaredMethod(String, Class...)
	 * @throws IllegalArgumentException if the return type of the method specified is not equal to {@code returnType}
	 */
	public <R> Method getDeclaredMethod(Class<R> returnType, String methodName, Class<?>... args) {
		Method retVal = getDeclaredMethod(methodName, args);
		// TODO probably needs to allow sub-classes?
		Preconditions.checkArgument(returnType == retVal.getReturnType());
		return retVal;
	}
	
	/**
	 * this method does not verify the return-type (so defers unchecked casting).
	 * @see Class#getMethod(String, Class...)
	 */
	private <R> Method getPublicMethod(String methodName, Class<?>... args) {
		final Method retVal;
		try {
			retVal = CLS.getMethod(methodName, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		return retVal;
	}
	/**
	 * this method does not verify the return-type (so defers unchecked casting).
	 * @see Class#getDeclaredMethod(String, Class...)
	 */
	private <R> Method getDeclaredMethod(String methodName, Class<?>... args) {
		final Method retVal;
		try {
			retVal = CLS.getDeclaredMethod(methodName, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		return retVal;
	}
	
	public class UnaryConstructorFunction<F> implements Function<F, T> {
		final Constructor<T> CTOR;
		UnaryConstructorFunction(Class<F> fromClass) {
			this(getConstructor(fromClass));
		}
		UnaryConstructorFunction(Constructor<T> ctor) {
			CTOR = ctor;
		}
		
		/** @throws RuntimeException */
		@Override
		public T apply(F input) {
			try {
				return CTOR.newInstance(input);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
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
				try {
					return CTOR.newInstance(ENCLOSING_INSTANCE, input);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	/** map from an instance to a nullary instance-method's return value */
	class NullaryMethodFunction<R> implements Function<T, R> {
		final Method M;
		NullaryMethodFunction(String methodName) {
			M = getDeclaredMethod(methodName);
		}
		NullaryMethodFunction(Class<R> returnType, String methodName) {
			M = getDeclaredMethod(returnType, methodName);
		}
		
		@Override
		public R apply(T input) {
			final Object retVal;
			try {
				retVal = M.invoke(input);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			
			@SuppressWarnings("unchecked")
			R r = (R) retVal;
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
			
			public UnaryMethodFunction(Class<V> returnType, String methodName, Class<K> argType) {
				M = (getDeclaredMethod(returnType, methodName, argType));
				
				Preconditions.checkArgument(
						isStatic() == Modifier.isStatic(M.getModifiers()),
						"static InstanceReference can't have non-static method: %s", M);
			}
			
			@Override
			public V apply(K input) {
				final Object retVal;
				try {
					retVal = M.invoke(I, input);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				
				@SuppressWarnings("unchecked")
				V v = (V) retVal;
				return v;
			}
		}
	}
}
