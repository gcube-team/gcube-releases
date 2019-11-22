package org.gcube.common.calls.interceptors;

import org.gcube.common.authorization.library.provider.CalledMethodProvider;
import org.gcube.common.calls.Call;
import org.gcube.common.calls.Interceptor;
import org.gcube.common.calls.Request;
import org.gcube.common.calls.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalledMethodInterceptor implements Interceptor {

	private Logger logger = LoggerFactory.getLogger(CalledMethodInterceptor.class);

	public static final String calledMethodHeader="gcube-method";

	@Override
	public void handleRequest(Request request, Call call) {
		String calledMethod = CalledMethodProvider.instance.get();

		request.addHeader(calledMethodHeader, calledMethod);
		logger.trace("called method set in the header is  "+calledMethod);
		
	}

	@Override
	public void handleResponse(Response context, Call callContext) {}

}
