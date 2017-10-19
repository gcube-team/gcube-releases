package org.gcube.common.clients.builders;

import java.net.URI;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.gcube.common.clients.builders.SingletonBuilderAPI.Builder;
import org.gcube.common.clients.builders.SingletonBuilderAPI.FinalClause;
import org.gcube.common.clients.builders.SingletonBuilderAPI.SecondClause;
import org.gcube.common.clients.cache.EndpointCache;
import org.gcube.common.clients.config.Property;
import org.gcube.common.clients.delegates.ProxyPlugin;
import org.gcube.common.clients.queries.Query;

/**
 * Partial implementation of proxy builders for singleton services, i.e. stateful services with a known single instance.
 * 
 * @author Fabio Simeoni
 * 
 * @param <A> the type of service addresses
 * @param <S> the type of service stubs
 * @param <P> the type of service proxies
 */
public abstract class AbstractSingletonBuilder<A,S,P> extends AbstractBuilder<A,S,P> implements Builder<A,P>,SecondClause<P>,FinalClause<P> {

	/**
	 * Constructs an instance with a given {@link ProxyPlugin}, and {@link EndpointCache}, and zero or more default {@link Property}s.
	 * @param plugin the plugin
	 * @param plugin the cache
	 * @param properties the properties
	 */
    protected AbstractSingletonBuilder(ProxyPlugin<A,S,P> plugin, EndpointCache<A> cache,Property<?> ... properties) {
		super(plugin,cache,properties);
	}

	@Override
	public SecondClause<P> matching(Query<A> query) {
		setQuery(query);
		return this;
	};

   @Override
	public SecondClause<P> at(String host, int port) {
		setAddress(AddressingUtils.address(contextPath(),plugin().name(), host, port));
		return this;
	}

	@Override
	public SecondClause<P> at(URL address) {
		setAddress(AddressingUtils.address(contextPath(),plugin().name(), address));
		return this;
	}

	@Override
	public SecondClause<P> at(URI address) {
		setAddress(AddressingUtils.address(contextPath(),plugin().name(), address));
		return this;
	}

	@Override
	public FinalClause<P> withTimeout(int duration, TimeUnit unit) {
		setTimeout((int) unit.toMillis(duration));
		return this;
	}
	
	@Override
	public SecondClause<P> with(Property<?> property) {
		addProperty(property);
		return this;
	}
	
	@Override
	public <T> SecondClause<P> with(String name, T value) {
		addProperty(new Property<T>(name, value));
		return this;
	}
}
