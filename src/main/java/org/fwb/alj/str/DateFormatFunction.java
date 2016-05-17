package org.fwb.alj.str;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Function;

/**
 * functional analog to {@link SimpleDateFormat}.
 * 
 * in addition to bridging between {@link DateFormat} and {@link Function},
 * the main purpose of this class is to wrap DateFormat logic
 * with a thread-safe instance (which DateFormats are not).
 */
public class DateFormatFunction implements Function<Date, String> {
	/** maps from a {@link SimpleDateFormat} format-string to a DateFormatFunction */
	public static final Function<String, DateFormatFunction> META = new Function<String, DateFormatFunction>() {
		@Override
		public DateFormatFunction apply(String input) {
			return new DateFormatFunction(input);
		}
	};
	
	public final Function<String, Date> REVERSE = new Function<String, Date>() {
		/** @throws RuntimeException wrapping {@link ParseException} */
		@Override
		public Date apply(String source) {
			try {
				return parse(source);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	};
//	/** alternative for serializing long timestamps rather than Date instances */
//	public final Function<Long, String> FROM_INT = new Function<Long, String>() {
//		@Override
//		public String apply(Long input) {
//			return newDateFormat().format(input);
//		}
//	};
	
	final String PATTERN;
	
	/**
	 * @param pattern a {@link SimpleDateFormat} pattern
	 */
	public DateFormatFunction(String pattern) {
		PATTERN = pattern;
		
		// checks and throws the appropriate exceptions if pattern is invalid (discard result)
		newDateFormat();
	}
	
	DateFormat newDateFormat() {
		// TODO is it worth considering storing a ThreadLocal instance?
		// I presume that would provide non-trivial performance gains
		// in the case of a single thread applying this function many times.
		return new SimpleDateFormat(PATTERN);
	}
	
	@Override
	public String apply(Date input) {
		return newDateFormat().format(input);
	}
	
	/**
	 * the reverse-function.
	 * @see #REVERSE
	 */
	public Date parse(String source) throws ParseException {
		return newDateFormat().parse(source);
	}
}
