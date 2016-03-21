package org.fwb.alj.col;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;

public class MapUtil {
	/** @deprecated static utilities only */
	@Deprecated
	private MapUtil() { }
	
	/**
	 * @throws IllegalArgumentException if any key-collisions occur
	 */
	public <K, V> void putAllNoCollisions(Map<K, V> map,
			Iterator<Entry<? extends K, ? extends V>> entries) {
		while (entries.hasNext()) {
			Entry<? extends K, ? extends V> e = entries.next();
			// unfortunate double-lookup required
			Preconditions.checkArgument(! map.containsKey(e.getKey()),
					"collision for entry %s: %s", e, map);
			// presumably returns null
			map.put(e.getKey(), e.getValue());
		}
	}
}
