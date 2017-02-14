package org.gcube.common.homelibrary.jcr.workspace.util;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.homelibrary.jcr.repository.ServletName;
import org.gcube.common.scope.api.ScopeProvider;

public class TokenUtility {

	public static void setHeader(GetMethod getMethod) {
		AuthorizationEntry entry = null;
		try {
			entry = authorizationService().get(SecurityTokenProvider.instance.get());
			if (entry.getContext() == null)
				throw new IllegalArgumentException("context is null");
			
			getMethod.setRequestHeader(ServletName.GCUBE_TOKEN, SecurityTokenProvider.instance.get());

		}catch (Exception e) {
			getMethod.setRequestHeader(ServletName.GCUBE_SCOPE, ScopeProvider.instance.get());
		}
		
	}
	
	public static void setHeader(PostMethod getMethod) {
		AuthorizationEntry entry = null;
		try {
			entry = authorizationService().get(SecurityTokenProvider.instance.get());
			if (entry.getContext() == null)
				throw new IllegalArgumentException("context is null");
			
			getMethod.setRequestHeader(ServletName.GCUBE_TOKEN, SecurityTokenProvider.instance.get());

		}catch (Exception e) {
			getMethod.setRequestHeader(ServletName.GCUBE_SCOPE, ScopeProvider.instance.get());
		}
		
	}
	
	
}
