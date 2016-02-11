package org.fwb.collection.todo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fwb.collection.todo.Relation.Field;

import java.util.Set;

/**
 * TODO
 * facilities for this special class of collections
 * which is a list-of-fields (Relation)
 * to be applied to lists-of-values (Records).
 */
interface Relation extends List<Field>, Set<Field> {
	/** a map from field-name to record-value */
	Map<String, Object> getRecordMap(Record r);
	
	interface Field extends Entry<String, Class<?>> { }
	
	interface Record extends List<Object> { }
}
