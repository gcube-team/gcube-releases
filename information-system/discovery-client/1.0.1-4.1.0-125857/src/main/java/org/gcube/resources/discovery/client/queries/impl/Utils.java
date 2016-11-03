package org.gcube.resources.discovery.client.queries.impl;

public class Utils {

	public static void notNull(String name, Object value) throws IllegalArgumentException {
		if (value==null)
			throw new IllegalArgumentException("parameter "+ name+" is null");
	}
}
