package org.gcube.data.analysis.tabulardata.clientlibrary.auth;

import org.gcube.common.calls.Call;
import org.gcube.common.calls.Interceptor;
import org.gcube.common.calls.Request;
import org.gcube.common.calls.Response;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationProvider;
import org.gcube.data.analysis.tabulardata.commons.utils.AuthorizationToken;
import org.gcube.data.analysis.tabulardata.commons.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationInterceptor implements Interceptor {

	private static Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);
		
	
	
	@Override
	public void handleRequest(Request request, Call callContext) {
		
		AuthorizationToken authToken= AuthorizationProvider.instance.get();
				
		if (authToken!=null)
			try {
				request.addHeader(Constants.tabular_data_auth_header, AuthorizationToken.marshal(authToken));
				logger.trace("authorization token set in the header is  "+authToken);
			} catch (Exception e) {
				logger.error("error serializing AuthorizationToken",e);
			}	
	}

	@Override
	public void handleResponse(Response context, Call callContext) {}
	
}
