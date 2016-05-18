package org.fwb.alj.col;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * analogous to guava's {@link Maps#fromProperties},
 * except this operates as a mutable view,
 * not an immutable copy.
 * 
 * @deprecated not sure this massive implementation is worth it,
 * as I believe it has identical behavior to {@link #asMapOfStrings},
 * except that its #iterator is fail-slow while this one's fails on P#propertyNames.
 */
@Deprecated
class PropertiesMap extends AbstractMap<String, String> {
	public final Properties P;
	
	public PropertiesMap(Properties p) {
		P = p;
	}
	
	@Override
	public Set<Entry<String, String>> entrySet() {
		return ENTRIES;
	}
	
	final Set<Entry<String, String>> ENTRIES = new AbstractSet<Entry<String, String>>() {
		/**
		 * TODO it'd be really nice to have a "more-live" view here, whatever that means, than P.propertyNames().
		 * in particular I think the Hashtable.entrySet method is "live-er," at least having fail-fast behavior.
		 * that could safely (if not beautifully) be used with type-checks on each element,
		 * or some other more deliberate hand-building of the Set's implementation
		 */
		@Override
		public Iterator<Entry<String, String>> iterator() {
			// this would throw delayed CCE upon accessing any non-string element
			// but Property docs say #propertyNames actually throws it greedily up-front, so here's hoping.
			@SuppressWarnings("unchecked")
			Enumeration<String> e = (Enumeration<String>) P.propertyNames();
			
			return Iterators.transform(
					Iterators.forEnumeration(e),
					ENTRY);
		}
		
		@Override
		public int size() {
			return P.size();
		}
	};
	
	final Function<String, Entry<String, String>> ENTRY = new Function<String, Entry<String, String>>() {
		@Override
		public Entry<String, String> apply(String input) {
			return new PropertiesEntry(input);
		}
	};
	class PropertiesEntry implements Entry<String, String> {
		final String KEY;
		PropertiesEntry(String key) {
			KEY = key;
		}
		
		@Override
		public String getKey() {
			return KEY;
		}
		@Override
		public String getValue() {
			return P.getProperty(KEY);
		}
		/** throws ClassCastException if the previous value was a non-String instance */
		@Override
		public String setValue(String value) {
			return (String) P.setProperty(getKey(), value);
		}
	}
	
	/**
	 * this cheater method returns the given Properties
	 * referenced as a Map of Strings
	 * rather than a Map of Objects as it is implemented for legacy compatibility.
	 * 
	 * @deprecated WARNING this is a cheater method with unchecked casting,
	 * so at runtime the return-value's contents may not honor its reference signature.
	 * @see PropertiesMap
	 */
	@Deprecated
	@SuppressWarnings({"unchecked", "rawtypes"})
	static Map<String, String> asMapOfStrings(Properties p) {
		return (Map<String, String>) (Map) p;
//		@SuppressWarnings({"unchecked", "rawtypes"})
//		Map<String, String> retVal = (Map<String, String>) (Map) p;
//		return retVal;
	}
}