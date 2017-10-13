package org.gcube.data.analysis.rconnector.client;

import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.gcube.common.calls.Interceptors;
import org.gcube.common.calls.Request;
import org.gcube.common.scope.api.ScopeProvider;

public class JaxRSRequestFilter implements ClientRequestFilter {

	private GcubeService service;

	public JaxRSRequestFilter(GcubeService service) {
		super();
		this.service = service;
	}

	@Override
	public void filter(final ClientRequestContext rc) throws IOException {
		System.out.println("request intercepted");
		if (ScopeProvider.instance.get()!=null){
			Request requestContext = Interceptors.executeRequestChain(service.call());

			for (Entry<String, String> entry: requestContext.getHeaders()){
				System.out.println("setting "+entry.getKey()+" "+entry.getValue());
				rc.getHeaders().put(entry.getKey(), Collections.singletonList((Object)entry.getValue()));	
			}
		}
	}
}
