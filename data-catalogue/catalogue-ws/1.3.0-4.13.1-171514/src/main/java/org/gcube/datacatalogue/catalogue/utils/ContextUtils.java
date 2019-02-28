package org.gcube.datacatalogue.catalogue.utils;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.ClientType;
import org.gcube.common.authorization.library.provider.AuthorizationProvider;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;

/**
 * @author Luca Frosini (ISTI - CNR)
 */
public class ContextUtils {
	
	private ContextUtils(){}
	
	public static String getContext() throws ObjectNotFound, Exception {
		String context = ScopeProvider.instance.get();
		if (context == null) {
			String token = SecurityTokenProvider.instance.get();
			try {
				return org.gcube.common.authorization.client.Constants.authorizationService().get(token).getContext();
			}catch (Exception e) {
				new RuntimeException(e);
			}
		}
		return context;
	}
	
	public static String getUsername() {
		return AuthorizationProvider.instance.get().getClient().getId();
		
	}
	
	public static boolean isApplication() {
		return AuthorizationProvider.instance.get().getClient().getType().equals(ClientType.EXTERNALSERVICE);
	}
	
	
}
