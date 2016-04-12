package org.gcube.common.vremanagement.whnmanager.client.proxies;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.gcube.resourcemanagement.whnmanager.api.Types.ScopeRIParams;
import org.gcube.resourcemanagement.whnmanager.api.WhnManager;
import org.gcube.resourcemanagement.whnmanager.api.exception.GCUBEUnrecoverableException;
import org.gcube.resourcemanagement.whnmanager.api.types.AddScopeInputParams;
import org.gcube.resourcemanagement.whnmanager.api.types.ScopeRIParams;

public class DefaultWHNManagerProxy implements WHNManagerProxy{
	
	ProxyDelegate<WhnManager> delegate;
	
	private static Logger logger = LoggerFactory.getLogger(DefaultWHNManagerProxy.class); 
	
	public DefaultWHNManagerProxy(ProxyDelegate<WhnManager> config) {
		this.delegate = config;
	}

	@Override
	public boolean removeScope(final String identifier) throws GCUBEUnrecoverableException {
		Call<WhnManager, Boolean> call = new Call<WhnManager, Boolean>() {

			@Override
			public Boolean call(WhnManager endpoint) throws Exception {
				return endpoint.removeScope(identifier);
			}
		};
		try{
			return delegate.make(call);
		}catch (GCUBEUnrecoverableException e) {
			logger.error("no task found with id {}",identifier);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public boolean addScope(final AddScopeInputParams params) throws GCUBEUnrecoverableException {
		Call<WhnManager, Boolean> call = new Call<WhnManager, Boolean>() {

			@Override
			public Boolean call(WhnManager endpoint) throws Exception {
				return endpoint.addScope(params);
			}
		};
		try{
			return delegate.make(call);
		}catch (GCUBEUnrecoverableException e) {
			logger.error("no task found with id {}", params);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public boolean addRIToScope(final ScopeRIParams params) throws GCUBEUnrecoverableException {
		Call<WhnManager, Boolean> call = new Call<WhnManager, Boolean>() {

			@Override
			public Boolean call(WhnManager endpoint) throws Exception {
				return endpoint.addRIToScope(params);
			}
		};
		try{
			return delegate.make(call);
		}catch (GCUBEUnrecoverableException e) {
			logger.error("no task found with id {}", params);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public boolean removeRIFromScope(final ScopeRIParams params) throws GCUBEUnrecoverableException {
		Call<WhnManager, Boolean> call = new Call<WhnManager, Boolean>() {

			@Override
			public Boolean call(WhnManager endpoint) throws Exception {
				return endpoint.removeRIFromScope(params);
			}
		};
		try{
			return delegate.make(call);
		}catch (GCUBEUnrecoverableException e) {
			logger.error("no task found with id {}", params);
			throw e;
		}catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

}
