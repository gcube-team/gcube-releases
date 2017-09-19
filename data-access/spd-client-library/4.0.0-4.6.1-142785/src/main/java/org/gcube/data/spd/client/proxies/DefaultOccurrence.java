package org.gcube.data.spd.client.proxies;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.ResultElementRecordIterator;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.service.types.MultiLocatorResponse;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;

public class DefaultOccurrence implements OccurrenceClient {

	private final ProxyDelegate<WebTarget> delegate;

	public DefaultOccurrence(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}

	@Override
	public Stream<OccurrencePoint> getByIds(final List<String> ids) {
		Call<WebTarget, MultiLocatorResponse> call = new Call<WebTarget, MultiLocatorResponse>() {
			@Override
			public MultiLocatorResponse call(WebTarget manager) throws Exception {
				manager = manager.path("ids");
				Response response = manager.request().get(Response.class);			
				return response.readEntity(MultiLocatorResponse.class);
			}
		};
		try {
			MultiLocatorResponse result = delegate.make(call);
			ResultElementRecordIterator<OccurrencePoint> ri = new ResultElementRecordIterator<OccurrencePoint>(result.getEndpointId(), result.getInputLocator(), 2, TimeUnit.MINUTES);
			DefaultResultSet.sendInput(result.getEndpointId(), result.getOutputLocator(), Streams.convert(ids));
			return Streams.convert(ri);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Stream<OccurrencePoint> getByKeys(final List<String> keys) {
		Call<WebTarget, MultiLocatorResponse> call = new Call<WebTarget, MultiLocatorResponse>() {
			@Override
			public MultiLocatorResponse call(WebTarget manager) throws Exception {
				manager = manager.path("keys");
				Response response = manager.request().get(Response.class);
				return response.readEntity(MultiLocatorResponse.class);
			}
		};
		try {
			MultiLocatorResponse result = delegate.make(call);
			ResultElementRecordIterator<OccurrencePoint> ri = new ResultElementRecordIterator<OccurrencePoint>(result.getEndpointId(), result.getInputLocator(), 2, TimeUnit.MINUTES);
			DefaultResultSet.sendInput(result.getEndpointId(), result.getOutputLocator(), Streams.convert(keys));
			return Streams.convert(ri);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}

	}

}
