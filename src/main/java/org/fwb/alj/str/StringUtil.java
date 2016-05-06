package org.fwb.alj.str;

import java.util.Set;

import org.fwb.alj.reflect.ClassReference;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

public class StringUtil {
	static final Set<String> DEFAULT_PARSER_METHOD_NAMES = ImmutableSet.of(
			"valueOf",
			"fromString",
			"forString",
			"parse", "parseString");
	public static <T> Function<String, T> getDefaultParser(Class<T> cls) throws NoSuchMethodException {
		ClassReference<T> cr = new ClassReference<T>(cls);
		
		for (String methodName : DEFAULT_PARSER_METHOD_NAMES)
			try {
				return cr.STATIC.new UnaryMethodFunction<>(cls, methodName, String.class);
			} catch (NoSuchMethodException e) {
			}
		
		try {
			return cr.new UnaryConstructorFunction<String>(String.class);
		} catch (NoSuchMethodException e) {
		}
		
		throw new NoSuchMethodException("no string-parser found for: " + cls);
	}
}
