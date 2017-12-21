package org.gcube.data.spd.client.proxies;

import org.glassfish.jersey.client.ChunkedInput;

public interface ResultSetClient {

	ChunkedInput<String> getResultSet(String locator);

	void closeResultSet(String locator);
	
}
