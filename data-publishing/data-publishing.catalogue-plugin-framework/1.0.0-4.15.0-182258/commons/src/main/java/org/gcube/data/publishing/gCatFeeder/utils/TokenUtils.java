package org.gcube.data.publishing.gCatFeeder.utils;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;

public class TokenUtils {

	public static void setToken(String token) {
		SecurityTokenProvider.instance.set(token);
	}
	
	public static String getCurrentToken() {
		return SecurityTokenProvider.instance.get();
	}
	
	public static String getClientId(String token) throws ObjectNotFound, Exception {
		AuthorizationEntry entry = authorizationService().get(token);
		return entry.getClientInfo().getId();
	}
}
