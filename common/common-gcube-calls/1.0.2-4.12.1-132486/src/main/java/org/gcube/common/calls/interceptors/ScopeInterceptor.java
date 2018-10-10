package org.gcube.common.calls.interceptors;

import org.gcube.common.calls.Call;
import org.gcube.common.calls.Interceptor;
import org.gcube.common.calls.Request;
import org.gcube.common.calls.Response;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeInterceptor implements Interceptor {

	private Logger logger = LoggerFactory.getLogger(ScopeInterceptor.class);
	
	public static final String scope_header="gcube-scope";
	
	public void handleRequest(Request request, Call call) {
		
		String scope = ScopeProvider.instance.get();
		
		if (scope==null)
			logger.warn("scope is not set in this call");
		else{
			request.addHeader(scope_header, scope);
			logger.trace("scope set in the header is  "+scope);
		}
	}

	public void handleResponse(Response context, Call callContext) {}

	

}
