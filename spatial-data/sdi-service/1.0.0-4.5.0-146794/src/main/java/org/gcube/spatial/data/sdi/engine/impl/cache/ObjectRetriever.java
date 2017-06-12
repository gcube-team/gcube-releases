package org.gcube.spatial.data.sdi.engine.impl.cache;

import org.gcube.spatial.data.sdi.engine.impl.faults.ConfigurationNotFoundException;

public interface ObjectRetriever<T> {

	public T getObject()throws ConfigurationNotFoundException;
	
}
