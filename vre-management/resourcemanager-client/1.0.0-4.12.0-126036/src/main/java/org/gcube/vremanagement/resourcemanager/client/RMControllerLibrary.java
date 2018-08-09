package org.gcube.vremanagement.resourcemanager.client;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidOptionsException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.fws.RMControllerServiceJAXWSStubs;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.CreateScopeParameters;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.OptionsParameters;
import org.gcube.vremanagement.resourcemanager.client.interfaces.RMControllerInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public class RMControllerLibrary implements RMControllerInterface{
	
	private final ProxyDelegate<RMControllerServiceJAXWSStubs> delegate;

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	
	public RMControllerLibrary(ProxyDelegate<RMControllerServiceJAXWSStubs> delegate) {
		this.delegate=delegate;

	}


	@Override
	public Empty changeScopeOptions(final OptionsParameters options)
			throws InvalidScopeException, InvalidOptionsException {
		Call<RMControllerServiceJAXWSStubs,Empty> call = new Call<RMControllerServiceJAXWSStubs,Empty>() {
			@Override 
			public Empty call(RMControllerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.ChangeScopeOptions(options);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw again(e).as(InvalidOptionsException.class,InvalidScopeException.class);
		}
	}


	@Override
	public String disposeScope(final String scope) throws InvalidScopeException {
		Call<RMControllerServiceJAXWSStubs,String> call = new Call<RMControllerServiceJAXWSStubs,String>() {
			@Override 
			public String call(RMControllerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.DisposeScope(scope);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw again(e).as(InvalidScopeException.class);
		}
	}


	@Override
	public Empty createScope(final CreateScopeParameters params)
			throws InvalidScopeException, InvalidOptionsException {
		Call<RMControllerServiceJAXWSStubs,Empty> call = new Call<RMControllerServiceJAXWSStubs,Empty>() {
			@Override 
			public Empty call(RMControllerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.CreateScope(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw again(e).as(InvalidScopeException.class,InvalidOptionsException.class);
		}
	}


}
