package org.gcube.common.authorization.client;

import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.gcube.common.authorization.client.plugin.AuthorizationPlugin;
import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.clients.ProxyBuilder;
import org.gcube.common.clients.ProxyBuilderImpl;

public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "AuthorizationService";

	/** Service class. */
	public static final String SERVICE_CLASS = "Common";
	
	public static final String CONTEXT_SERVICE_NAME="authorization-service";
	
	public static final int DEFAULT_TIMEOUT= (int) TimeUnit.SECONDS.toMillis(10);

	private static final String TNS = "http://gcube-system.org/";
	
	public static final QName AUTHORIZATION_QNAME = new QName(TNS, "authorization-service");

	public static final String SCOPE_HEADER_ENTRY = "gcube-scope";
	
	public static final long TIME_TO_LIVE_CACHE_IN_MILLIS = (60*1000)*60; //1 hour
	
	public static ProxyBuilder<AuthorizationProxy> authorizationService() {
		return new ProxyBuilderImpl<String,AuthorizationProxy>(new AuthorizationPlugin());
	}
}
