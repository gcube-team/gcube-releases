package org.gcube.common.vremanagement.ghnmanager.client;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.common.clients.exceptions.ServiceException;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.common.vremanagement.ghnmanager.client.fws.GHNManagerServiceJAXWSStubs;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.AddScopeInputParams;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.RIData;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ScopeRIParams;
import org.gcube.common.vremanagement.ghnmanager.client.fws.Types.ShutdownOptions;
import org.gcube.common.vremanagement.ghnmanager.client.proxies.GHNManagerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Andrea
 *
 */
public class GHNManagerLibrary implements GHNManagerService{
	
	private final ProxyDelegate<GHNManagerServiceJAXWSStubs> delegate;

	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	
	public GHNManagerLibrary(ProxyDelegate<GHNManagerServiceJAXWSStubs> delegate) {
		this.delegate=new AsyncProxyDelegate<GHNManagerServiceJAXWSStubs>(delegate);

	}

	@Override
	public boolean addScope(final AddScopeInputParams params) throws Exception {
		
		Call<GHNManagerServiceJAXWSStubs,Boolean> call = new Call<GHNManagerServiceJAXWSStubs,Boolean>() {
			@Override 
			public Boolean call(GHNManagerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.addScope(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}

	@Override
	public boolean removeScope(final String scope) throws Exception {
		Call<GHNManagerServiceJAXWSStubs,Boolean> call = new Call<GHNManagerServiceJAXWSStubs,Boolean>() {
			@Override 
			public Boolean call(GHNManagerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.removeScope(scope);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
	}

	@Override
	public boolean addRIToScope(final ScopeRIParams params) throws Exception {
		Call<GHNManagerServiceJAXWSStubs,Boolean> call = new Call<GHNManagerServiceJAXWSStubs,Boolean>() {
			@Override 
			public Boolean call(GHNManagerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.addRIToScope(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
	}

	@Override
	public boolean activateRI(final RIData params) throws Exception {
		Call<GHNManagerServiceJAXWSStubs,Boolean> call = new Call<GHNManagerServiceJAXWSStubs,Boolean>() {
			@Override 
			public Boolean call(GHNManagerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.activateRI(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
	}

	@Override
	public boolean deactivateRI(final RIData params) throws Exception {
		Call<GHNManagerServiceJAXWSStubs,Boolean> call = new Call<GHNManagerServiceJAXWSStubs,Boolean>() {
			@Override 
			public Boolean call(GHNManagerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.deactivateRI(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
	}

	@Override
	public boolean removeRIFromScope(final ScopeRIParams params) throws Exception {
		Call<GHNManagerServiceJAXWSStubs,Boolean> call = new Call<GHNManagerServiceJAXWSStubs,Boolean>() {
			@Override 
			public Boolean call(GHNManagerServiceJAXWSStubs endpoint) throws Exception {
				return  endpoint.removeRIFromScope(params);

			}
		};
		try {
			return delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
	}


	@Override
	public void shutdown(final ShutdownOptions options) throws Exception {
		Call<GHNManagerServiceJAXWSStubs,Empty> call = new Call<GHNManagerServiceJAXWSStubs,Empty>() {
			@Override 
			public Empty call(GHNManagerServiceJAXWSStubs endpoint) throws Exception {
				 return endpoint.shutdown(options);

			}
		};
		try {
			 delegate.make(call);
		}

		catch(Exception e) {
			throw new ServiceException (e);
		}
		
	}


}
