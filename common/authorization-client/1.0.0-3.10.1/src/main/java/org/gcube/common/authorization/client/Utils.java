package org.gcube.common.authorization.client;

public class Utils {

	static void notNull(String message,Object o) {
		if (o==null)
			throw new IllegalArgumentException(o+" cannot be null");
	}
}
