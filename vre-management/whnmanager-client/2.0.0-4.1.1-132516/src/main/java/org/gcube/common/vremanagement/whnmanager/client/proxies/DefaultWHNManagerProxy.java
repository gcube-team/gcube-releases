package org.gcube.common.vremanagement.whnmanager.client.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.resourcemanagement.whnmanager.api.WhnManager;
import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.gcube.resourcemanagement.whnmanager.api.Types.ScopeRIParams;

public class DefaultWHNManagerProxy implements WHNManagerProxy{
	
	ProxyDelegate<WhnManager> delegate;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultWHNManagerProxy.class); 
	
	public DefaultWHNManagerProxy(ProxyDelegate<WhnManager> config) {
		this.delegate = config;
	}

	@Override
	public boolean removeFromContext(final String context) throws GCUBEUnrecoverableException {
		Call<WhnManager, Boolean> call = new Call<WhnManager, Boolean>() {

			@Override
			public Boolean call(WhnManager endpoint) throws Exception {
				return endpoint.removeFromContext(context);
			}
		};
		try{
			return delegate.make(call);
		}catch (GCUBEUnrecoverableException e) {
			logger.error("no task found with id {}",context);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public boolean addToContext(final String context) throws GCUBEUnrecoverableException {
		Call<WhnManager, Boolean> call = new Call<WhnManager, Boolean>() {

			@Override
			public Boolean call(WhnManager endpoint) throws Exception {
				return endpoint.addToContext(context);
			}
		};
		try{
			return delegate.make(call);
		}catch (GCUBEUnrecoverableException e) {
			logger.error("no task found with id {}", context);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}


}
