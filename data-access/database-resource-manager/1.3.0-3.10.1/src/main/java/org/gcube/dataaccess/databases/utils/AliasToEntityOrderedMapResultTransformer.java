package org.gcube.dataaccess.databases.utils;

import java.util.LinkedHashMap;
//import java.util.List;
import java.util.Map;

//import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
import org.hibernate.transform.BasicTransformerAdapter;

/**
 * Class that allows to recover data from database through the class
 * BasicTransformerAdapter of Hibernate. It allows to retrieve columns names and
 * values.
 */
public class AliasToEntityOrderedMapResultTransformer extends
		BasicTransformerAdapter {

	public static final AliasToEntityOrderedMapResultTransformer INSTANCE = new AliasToEntityOrderedMapResultTransformer();

	/**
	 * Disallow instantiation of AliasToEntityOrderedMapResultTransformer .
	 */
	private AliasToEntityOrderedMapResultTransformer() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object transformTuple(Object[] tuple, String[] aliases) {
		// linkedhashmap to get table column name in order
		Map result = new LinkedHashMap(tuple.length);
		for (int i = 0; i < tuple.length; i++) {
			String alias = aliases[i];
			if (alias != null) {
				result.put(alias, tuple[i]);
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isTransformedValueATupleElement(String[] aliases,
			int tupleLength) {
		return false;
	}

	/**
	 * Serialization hook for ensuring singleton uniqueing.
	 * 
	 * @return The singleton instance : {@link #INSTANCE}
	 */
	private Object readResolve() {
		return INSTANCE;
	}

}
