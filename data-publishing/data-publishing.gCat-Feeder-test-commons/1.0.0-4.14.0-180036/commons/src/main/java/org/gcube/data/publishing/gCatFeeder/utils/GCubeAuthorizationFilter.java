package org.gcube.data.publishing.gCatFeeder.utils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.gcube.common.calls.Call;
import org.gcube.common.calls.Interceptors;
import org.gcube.common.calls.Request;

public class GCubeAuthorizationFilter implements ClientRequestFilter {

	
	@Override
	public void filter(final ClientRequestContext rc) throws IOException {
		if (ContextUtils.getCurrentScope()!=null){
			Request requestContext = Interceptors.executeRequestChain(new Call());

			for (Entry<String, String> entry: requestContext.getHeaders()){				
				rc.getHeaders().put(entry.getKey(), Collections.singletonList((Object)entry.getValue()));	
			}
		}
	}
}
