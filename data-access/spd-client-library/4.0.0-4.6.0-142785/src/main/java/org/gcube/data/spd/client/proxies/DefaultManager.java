package org.gcube.data.spd.client.proxies;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.ResultElementRecordIterator;
import org.gcube.data.spd.model.PluginDescription;
import org.gcube.data.spd.model.exceptions.InvalidQueryException;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.model.service.types.MultiLocatorResponse;
import org.gcube.data.spd.model.service.types.PluginDescriptions;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;

public class DefaultManager implements ManagerClient {

	private final ProxyDelegate<WebTarget> delegate;

	
	public DefaultManager(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}

	@Override
	public <T extends ResultElement> Stream<T> search(final String query)
			throws InvalidQueryException, UnsupportedPluginException,
			UnsupportedCapabilityException {
		Call<WebTarget, MultiLocatorResponse> call = new Call<WebTarget, MultiLocatorResponse>() {
			@Override
			public MultiLocatorResponse call(WebTarget manager) throws Exception {
				Response response =  manager.path("search").queryParam("query", query).request().get(Response.class);
				return response.readEntity(MultiLocatorResponse.class);
			}
		};
		try {
			MultiLocatorResponse result = delegate.make(call);
			System.out.println("MULTILOCACATOR IS "+result);
			ResultElementRecordIterator<T> ri = new ResultElementRecordIterator<T>(result.getEndpointId(), result.getInputLocator(), 2, TimeUnit.MINUTES);
			return Streams.convert(ri);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	@Override
	public List<PluginDescription> getPluginsDescription() {
		Call<WebTarget, List<PluginDescription>> call = new Call<WebTarget, List<PluginDescription>>() {
			@Override
			public List<PluginDescription> call(WebTarget manager) throws Exception {
				return  manager.path("providers").request().get(PluginDescriptions.class).getDescriptions();
			}
		};
		try {
			return delegate.make(call);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

}
