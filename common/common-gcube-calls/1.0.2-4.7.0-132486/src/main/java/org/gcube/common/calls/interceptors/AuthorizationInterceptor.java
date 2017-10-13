package org.gcube.common.calls.interceptors;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.calls.Call;
import org.gcube.common.calls.Interceptor;
import org.gcube.common.calls.Request;
import org.gcube.common.calls.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizationInterceptor implements Interceptor {

	private Logger logger = LoggerFactory.getLogger(AuthorizationInterceptor.class);

	public static final String token_header="gcube-token";

	@Override
	public void handleRequest(Request request, Call call) {
		String token = SecurityTokenProvider.instance.get();

		if (token==null)
			logger.warn("security token is not set");
		else{
			request.addHeader(token_header, token);
			logger.trace("security token set in the header is  "+token);
		}
	}

	@Override
	public void handleResponse(Response context, Call callContext) {}
}
