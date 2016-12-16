package org.gcube.common.clients;

import javax.xml.ws.EndpointReference;

import org.gcube.common.clients.cache.DefaultEndpointCache;
import org.gcube.common.clients.cache.EndpointCache;

/**
 * Library-wide utilities
 * 
 * @author Fabio Simeoni
 *
 */
public class Utils {

	/**
	 * Shared endpoint cache.
	 */
	public static EndpointCache<EndpointReference> globalCache = new DefaultEndpointCache<EndpointReference>();
}
