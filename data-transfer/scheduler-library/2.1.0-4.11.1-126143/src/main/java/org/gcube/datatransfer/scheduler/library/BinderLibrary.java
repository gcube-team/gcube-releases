package org.gcube.datatransfer.scheduler.library;

import javax.xml.ws.wsaddressing.W3CEndpointReference;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.scheduler.library.fws.BinderServiceJAXWSStubs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinderLibrary {
	private final AsyncProxyDelegate<BinderServiceJAXWSStubs> delegate;
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	public BinderLibrary(ProxyDelegate<BinderServiceJAXWSStubs> config) {
		this.delegate=new AsyncProxyDelegate<BinderServiceJAXWSStubs>(config);
	}


	public W3CEndpointReference bind(String nameOfClient){
		ScopeProvider.instance.set(ScopeProvider.instance.get());

		final String message=nameOfClient;
		Call<BinderServiceJAXWSStubs,W3CEndpointReference> call = new Call<BinderServiceJAXWSStubs,W3CEndpointReference>() {
			@Override 
			public W3CEndpointReference call(BinderServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.create(message);
			}
		};

		W3CEndpointReference result=null;
		try {
			result= delegate.make(call);
		}catch(Exception e) {
			logger.error("BinderLibrary - Exception when calling endpoint.checkIn(message)");
			e.printStackTrace();
		}

		return result;
	}
}
