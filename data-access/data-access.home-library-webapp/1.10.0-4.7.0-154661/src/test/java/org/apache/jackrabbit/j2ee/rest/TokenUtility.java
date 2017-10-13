package org.apache.jackrabbit.j2ee.rest;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.net.HttpURLConnection;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;

public class TokenUtility {
	
	public static final String GCUBE_TOKEN 				= "gcube-token";
	public static final String GCUBE_SCOPE 				= "gcube-scope";
	
	public static void setHeader(HttpURLConnection connection) {
		AuthorizationEntry entry = null;
		try {
			entry = authorizationService().get(SecurityTokenProvider.instance.get());
			if (entry.getContext() == null)
				throw new IllegalArgumentException("context is null");
			
			connection.setRequestProperty(GCUBE_TOKEN, SecurityTokenProvider.instance.get());

		}catch (Exception e) {
			connection.setRequestProperty(GCUBE_SCOPE, ScopeProvider.instance.get());
		}
		
	}

	public static void setHeader(MultipartUtility multipart) {
		AuthorizationEntry entry = null;
		try {
			entry = authorizationService().get(SecurityTokenProvider.instance.get());
			if (entry.getContext() == null)
				throw new IllegalArgumentException("context is null");
			
			multipart.addHeaderField(GCUBE_TOKEN, SecurityTokenProvider.instance.get());

		}catch (Exception e) {
			multipart.addHeaderField(GCUBE_SCOPE, ScopeProvider.instance.get());
		}
	}
	
}
