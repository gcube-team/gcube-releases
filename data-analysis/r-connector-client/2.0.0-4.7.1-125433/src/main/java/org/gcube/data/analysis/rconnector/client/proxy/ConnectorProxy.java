package org.gcube.data.analysis.rconnector.client.proxy;

import java.net.URI;

public interface ConnectorProxy {

	URI connect(Long tabularResourceId);

	URI connect();
	
}
