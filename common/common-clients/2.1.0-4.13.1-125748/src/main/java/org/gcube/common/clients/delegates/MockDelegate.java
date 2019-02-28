package org.gcube.common.clients.delegates;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.builders.AbstractBuilder;
import org.gcube.common.clients.config.AbstractConfig;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.config.ProxyConfig;

/**
 * A {@link ProxyDelegate} for mock testing.
 * 
 * @author Fabio Simeoni
 *
 * @param <S> the type of service endpoint stubs used by {@link Call}s
 */
public class MockDelegate<S> implements ProxyDelegate<S> {

	/**
	 * Creates an instance with a {@link ProxyPlugin} and a mock endpoints.
	 * @param plugin the plugin
	 * @param endpoint the endpoint
	 */
	public static <S> ProxyDelegate<S> mockDelegate(ProxyPlugin<?, S, ?> plugin,S endpoint) {
		return new MockDelegate<S>(plugin, endpoint);
	};
	
	private ProxyConfig<?,S> config;
	private S mockEndpoint;


	@SuppressWarnings("all")
	private MockDelegate(ProxyPlugin<?, S, ?> plugin,S endpoint) {
		this.config = new AbstractConfig(plugin) {};
		this.config().addProperty(Property.timeout(AbstractBuilder.defaultTimeout));
		this.mockEndpoint=endpoint;
	}
	
	@Override
	public <V> V make(Call<S, V> call) throws Exception {
		try {
			return call.call(mockEndpoint);
		}
		catch(Exception e) {
			throw config.plugin().convert(e,config);
		}
	}
	
	@Override
	public ProxyConfig<?, S> config() {
		return config;
	}
}
