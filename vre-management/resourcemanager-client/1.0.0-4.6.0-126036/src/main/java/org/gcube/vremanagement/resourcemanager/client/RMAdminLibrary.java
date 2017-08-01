package org.gcube.vremanagement.resourcemanager.client;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;

import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.fws.RMAdminServiceJAXWSStubs;
import org.gcube.vremanagement.resourcemanager.client.interfaces.RMAdminInterface;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class RMAdminLibrary implements RMAdminInterface {
	
	private final ProxyDelegate<RMAdminServiceJAXWSStubs> delegate;

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	
	public RMAdminLibrary(ProxyDelegate<RMAdminServiceJAXWSStubs> delegate) {
		this.delegate=delegate;

	}


	@Override
	public Empty cleanSoftwareState() throws InvalidScopeException {
		
		Call<RMAdminServiceJAXWSStubs,Empty> call = new Call<RMAdminServiceJAXWSStubs,Empty>() {
			@Override 
			public Empty call(RMAdminServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.CleanSoftwareState();

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}


}
