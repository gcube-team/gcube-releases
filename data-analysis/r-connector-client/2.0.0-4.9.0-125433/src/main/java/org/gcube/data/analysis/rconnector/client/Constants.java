package org.gcube.data.analysis.rconnector.client;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.WebTarget;
import javax.xml.namespace.QName;

import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;
import org.gcube.data.analysis.rconnector.client.plugin.ConnectorPlugin;
import org.gcube.data.analysis.rconnector.client.proxy.ConnectorProxy;

public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "RConnector";

	/** Service class. */
	public static final String SERVICE_CLASS = "DataAnalysis";
	
	public static final String CONTEXT_SERVICE_NAME="r-connector";
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);

	private static final String TNS = "http://gcube-system.org/";
	
	public static final QName CONNECTOR_QNAME = new QName(TNS, "connector");
	
	public static ProxyBuilder<ConnectorProxy> rConnector() {
		return new ProxyBuilderImpl<WebTarget,ConnectorProxy>(new ConnectorPlugin());
	}

}
