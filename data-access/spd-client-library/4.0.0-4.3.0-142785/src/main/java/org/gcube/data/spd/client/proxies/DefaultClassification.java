package org.gcube.data.spd.client.proxies;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.data.spd.client.ResultElementRecordIterator;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.model.service.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.model.service.types.MultiLocatorResponse;
import org.gcube.data.streams.Stream;
import org.gcube.data.streams.dsl.Streams;

public class DefaultClassification implements ClassificationClient{

	private final ProxyDelegate<WebTarget> delegate;
		
	public DefaultClassification(ProxyDelegate<WebTarget> config){
		this.delegate = config;
	}

	@Override
	public Stream<TaxonomyItem> getTaxonChildrenById(final String id)
			throws UnsupportedPluginException, UnsupportedCapabilityException,
			InvalidIdentifierException {
		Call<WebTarget, MultiLocatorResponse> call = new Call<WebTarget, MultiLocatorResponse>() {
			@Override
			public MultiLocatorResponse call(WebTarget manager) throws Exception {
				Response response =  manager.path("children").path(id).request().get(Response.class);
				return response.readEntity(MultiLocatorResponse.class);
			}
		};
		try {
			MultiLocatorResponse result = delegate.make(call);
			ResultElementRecordIterator<TaxonomyItem> ri = new ResultElementRecordIterator<TaxonomyItem>(result.getEndpointId(), result.getInputLocator(), 2, TimeUnit.MINUTES);
			return Streams.convert(ri);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//TODO
	@Override
	public Stream<TaxonomyItem> getTaxaByIds(final List<String> ids) {
		return null;
		/*Call<WebTarget, ChunkedInput<String>> call = new Call<WebTarget, ChunkedInput<String>>() {
			@Override
			public ChunkedInput<String> call(WebTarget manager) throws Exception {
				return  null;			}
		};
		try {
			return null;
		}catch(Exception e) {
			throw new RuntimeException(e);
		}*/
	}

	@Override
	public Stream<TaxonomyItem> getTaxonTreeById(final String id)
			throws UnsupportedPluginException, UnsupportedCapabilityException,
			InvalidIdentifierException {
		Call<WebTarget, MultiLocatorResponse> call = new Call<WebTarget, MultiLocatorResponse>() {
			@Override
			public MultiLocatorResponse call(WebTarget manager) throws Exception {
				Response response =  manager.path("tree").path(id).request().get(Response.class);
				return response.readEntity(MultiLocatorResponse.class);
			}
		};
		try {
			MultiLocatorResponse result = delegate.make(call);
			ResultElementRecordIterator<TaxonomyItem> ri = new ResultElementRecordIterator<TaxonomyItem>(result.getEndpointId(), result.getInputLocator(), 2, TimeUnit.MINUTES);
	
			return Streams.convert(ri);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Stream<TaxonomyItem> getSynonymsById(final String id)
			throws UnsupportedPluginException, UnsupportedCapabilityException,
			InvalidIdentifierException {
		Call<WebTarget, MultiLocatorResponse> call = new Call<WebTarget, MultiLocatorResponse>() {
			@Override
			public MultiLocatorResponse call(WebTarget manager) throws Exception {
				Response response =  manager.path("synonyms").path(id).request().get(Response.class);
				return response.readEntity(MultiLocatorResponse.class);
			}
		};
		try {
			MultiLocatorResponse result = delegate.make(call);
			ResultElementRecordIterator<TaxonomyItem> ri = new ResultElementRecordIterator<TaxonomyItem>(result.getEndpointId(), result.getInputLocator(), 2, TimeUnit.MINUTES);
	
			return Streams.convert(ri);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
		
	}


}
