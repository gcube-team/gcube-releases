package org.gcube.vremanagement.resourcemanager.client;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesCreationException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.ResourcesRemovalException;
import org.gcube.vremanagement.resourcemanager.client.fws.*;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.AddResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.RemoveResourcesParameters;
import org.gcube.vremanagement.resourcemanager.client.interfaces.RMBinderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.gcube.common.clients.exceptions.FaultDSL.*;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class RMBinderLibrary implements RMBinderInterface{
	
	private final ProxyDelegate<RMBinderServiceJAXWSStubs> delegate;

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	
	public RMBinderLibrary(ProxyDelegate<RMBinderServiceJAXWSStubs> delegate) {
		this.delegate=delegate;

	}


	@Override
	public String addResources(final AddResourcesParameters params)
			throws ResourcesCreationException, InvalidScopeException {
		
		Call<RMBinderServiceJAXWSStubs,String> call = new Call<RMBinderServiceJAXWSStubs,String>() {
			@Override 
			public String call(RMBinderServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.AddResources(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw again(e).as(ResourcesCreationException.class,InvalidScopeException.class);
		}
		
	}


	@Override
	public String removeResources(final RemoveResourcesParameters params)
			throws ResourcesRemovalException, InvalidScopeException {
		
		Call<RMBinderServiceJAXWSStubs,String> call = new Call<RMBinderServiceJAXWSStubs,String>() {
			@Override 
			public String call(RMBinderServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.RemoveResources(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw again(e).as(ResourcesRemovalException.class,InvalidScopeException.class);
		}
		
	}


}
