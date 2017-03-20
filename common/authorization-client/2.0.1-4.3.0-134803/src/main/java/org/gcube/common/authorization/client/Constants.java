package org.gcube.common.authorization.client;

import org.gcube.common.authorization.client.proxy.AuthorizationProxy;
import org.gcube.common.authorization.client.proxy.DefaultAuthorizationProxy;

public class Constants {

	/** Service name. */
	public static final String SERVICE_NAME = "AuthorizationService";

	public static String CLIENT_ID_PARAM= "client_id";
	
	public static String CONTEXT_PARAM= "context";
	
	public static String ROLES_PARAM= "roles";
	
	public static final String TOKEN_HEADER_ENTRY = "gcube-token";
	
	public static final long TIME_TO_LIVE_CACHE_IN_MILLIS = (60*1000)*60; //1 hour
	
	public static AuthorizationProxy authorizationService() {
		return new DefaultAuthorizationProxy();
	}
}
