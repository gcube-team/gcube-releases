package org.gcube.data.transfer.library.client;

import java.io.IOException;
import java.util.Collections;
import java.util.Map.Entry;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import org.gcube.common.calls.Call;
import org.gcube.common.calls.Interceptors;
import org.gcube.common.calls.Request;
import org.gcube.data.transfer.library.utils.ScopeUtils;

public class AuthorizationFilter implements ClientRequestFilter {

	
	@Override
	public void filter(final ClientRequestContext rc) throws IOException {
		if (ScopeUtils.getCurrentScope()!=null){
			Request requestContext = Interceptors.executeRequestChain(new Call());

			for (Entry<String, String> entry: requestContext.getHeaders()){				
				rc.getHeaders().put(entry.getKey(), Collections.singletonList((Object)entry.getValue()));	
			}
		}
	}
}
