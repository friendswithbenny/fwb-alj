package org.fwb.alj.todo;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fwb.alj.col.SetUtil.SetAndList;
import org.fwb.alj.todo.Relation.Field;

/**
 * @deprecated TODO
 * facilities for this special class of collections
 * which is a list-of-fields (Relation)
 * to be applied to lists-of-values (Records).
 */
interface Relation extends SetAndList<Field> {
	/** a map from field-name to record-value */
	Map<String, Object> getRecordMap(Record r);
	
	interface Field extends Entry<String, Class<?>> { }
	
	interface Record extends List<Object> { }
}
