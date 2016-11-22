package org.fwb.alj.todo;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * @deprecated TODO consider. this is just a quick prototype.
 */
// TODO it appears the 'return' statements are actually bugs:
// http://stackoverflow.com/questions/25773567/recursive-merge-of-n-level-maps/36123154#comment-64745316
class MergeMap {
	/**
	 * a modified version of this:
	 * http://stackoverflow.com/questions/25773567/recursive-merge-of-n-level-maps#answer-29698326
	 * which is, in turn, a modified version of this:
	 * https://gist.github.com/aslakhellesoy/3858814
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	static void deepMerge(
			Map original,
			Map newMap) {
		
		for (Entry e : (Set<Entry>) newMap.entrySet()) {
			Object
				key = e.getKey(),
				value = e.getValue();
			
			// unfortunately, if null-values are allowed,
			// we suffer the performance hit of double-lookup
			if (original.containsKey(key)) {
				Object originalValue = original.get(key);
				
				if (Objects.equal(originalValue, value))
					// TODO (see top)
					return;
				
				if (originalValue instanceof Collection) {
					// this could be relaxed to simply to simply add instead of addAll
					// IF it's not a collection (still addAll if it is),
					// this would be a useful approach, but uncomfortably inconsistent, algebraically
					Preconditions.checkArgument(value instanceof Collection,
							"a non-collection collided with a collection: %s%n\t%s",
							value, originalValue);
					
					((Collection) originalValue).addAll((Collection) value);
					
					// TODO (see top)
					return;
				}
				
				if (originalValue instanceof Map) {
					Preconditions.checkArgument(value instanceof Map,
							"a non-map collided with a map: %s%n\t%s",
							value, originalValue);
					
					deepMerge((Map) originalValue, (Map) value);
					
					// TODO (see top)
					return;
				}
				
				// to over-ride instead of failing:
				// {@code original.put(key, value)}
				throw new IllegalArgumentException(String.format(
						"collision detected: %s%n%\torig:%s",
						value, originalValue));
				
			} else
				original.put(key, value);
		}
	}
}
