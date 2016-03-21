package org.fwb.alj.en;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import com.google.common.base.Function;

public class EnumUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private EnumUtil () { }
	
	public static final Function<Enum<?>, String> ENUM_NAME = new Function<Enum<?>, String>() {
		@Override
		public String apply(Enum<?> e) {
			return e.name();
		}
	};
	
	public static class EnumName<T extends Enum<T>> implements Function<T, String> {
		@Override
		public String apply(T t) {
			return t.name();
		}
	}
	
	public static final Function<Class<? extends Enum<?>>, EnumSet<? extends Enum<?>>> ENUM_VALUES =
			new Function<Class<? extends Enum<?>>, EnumSet<? extends Enum<?>>>() {
				@Override
				public EnumSet<? extends Enum<?>> apply(Class<? extends Enum<?>> c) {
					@SuppressWarnings({"rawtypes", "unchecked"})
					Class<Enum> c2 = (Class<Enum>) c;
					
					@SuppressWarnings("unchecked")
					EnumSet<? extends Enum<?>> retVal = EnumSet.allOf(c2);
					
					return retVal;
				}
			};
	
	public static class EnumValues<T extends Enum<T>> implements Function<Class<T>, EnumSet<T>> {
		@Override
		public EnumSet<T> apply(Class<T> c) {
			return EnumSet.allOf(c);
		}
	}
	
	public static final Function<Class<? extends Enum<?>>, List<? extends Enum<?>>> ENUM_VALUE_LIST =
			new Function<Class<? extends Enum<?>>, List<? extends Enum<?>>>() {
				@Override
				public List<? extends Enum<?>> apply(Class<? extends Enum<?>> c) {
					return Arrays.asList(c.getEnumConstants());
				}
			};
	
	public static class EnumValueList<T extends Enum<T>> implements Function<Class<T>, List<T>> {
		@Override
		public List<T> apply(Class<T> c) {
			return Arrays.asList(c.getEnumConstants());
		}
	}
	
	public static class EnumValueOf<T extends Enum<T>> implements Function<String, T> {
		final Class<T> CLS;
		public EnumValueOf(Class<T> cls) {
			CLS = cls;
		}
		@Override
		public T apply(String input) {
			return Enum.valueOf(CLS, input);
		}
	}
}
