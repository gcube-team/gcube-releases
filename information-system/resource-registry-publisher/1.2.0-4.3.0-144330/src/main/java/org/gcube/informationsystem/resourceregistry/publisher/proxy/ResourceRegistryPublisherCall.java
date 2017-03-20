package org.gcube.informationsystem.resourceregistry.publisher.proxy;

import java.io.IOException;
import java.net.URL;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.Call;
import org.gcube.informationsystem.resourceregistry.api.rest.httputils.HTTPCall;

class ResourceRegistryPublisherCall<C> implements Call<EndpointReference, C> {

	protected final Class<C> clazz;
	protected final HTTPCall<C> httpInputs;

	public ResourceRegistryPublisherCall(Class<C> clazz, HTTPCall<C> httpInputs) {
		this.clazz = clazz;
		this.httpInputs = httpInputs;
	}

	protected String getURLStringFromEndpointReference(
			EndpointReference endpoint) throws IOException {
		JaxRSEndpointReference jaxRSEndpointReference = new JaxRSEndpointReference(
				endpoint);
		return jaxRSEndpointReference.toString();
	}
	@Override
	public C call(EndpointReference endpoint) throws Exception {			
		String urlFromEndpointReference = getURLStringFromEndpointReference(endpoint);
		StringBuilder callUrl = new StringBuilder(urlFromEndpointReference);
		callUrl.append(httpInputs.getPath());
		URL url = new URL(callUrl.toString());
		return httpInputs.call(clazz, url, ResourceRegistryPublisher.class.getSimpleName());

	}
}