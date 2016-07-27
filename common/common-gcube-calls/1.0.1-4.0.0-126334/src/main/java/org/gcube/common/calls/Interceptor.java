package org.gcube.common.calls;

public interface Interceptor {

	void handleRequest(Request context, Call callContext);
	
	void handleResponse(Response context, Call callContext);

}
