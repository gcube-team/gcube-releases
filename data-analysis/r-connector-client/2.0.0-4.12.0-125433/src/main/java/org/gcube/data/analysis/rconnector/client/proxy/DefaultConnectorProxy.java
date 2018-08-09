package org.gcube.data.analysis.rconnector.client.proxy;

import static org.gcube.common.clients.exceptions.FaultDSL.again;

import java.net.URI;

import javax.ws.rs.client.WebTarget;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;

public class DefaultConnectorProxy implements ConnectorProxy {

	private final ProxyDelegate<WebTarget> delegate;
	
	public DefaultConnectorProxy(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}
	
	@Override
	public URI connect(final Long trId) {
		Call<WebTarget, URI> call = new Call<WebTarget, URI>() {

			@Override
			public URI call(WebTarget endpoint) throws Exception {
				return endpoint.path("connect/").path(trId.toString()).queryParam("gcube-token", SecurityTokenProvider.instance.get())
						.getUri();
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}

	@Override
	public URI connect() {
		Call<WebTarget, URI> call = new Call<WebTarget, URI>() {

			@Override
			public URI call(WebTarget endpoint) throws Exception {
				return endpoint.path("connect").queryParam("gcube-token", SecurityTokenProvider.instance.get()).getUri();
			}
		};
		try {
			return delegate.make(call);
		} catch (Exception e) {
			throw again(e).asServiceException();
		}
	}
	
}
