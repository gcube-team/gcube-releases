/**
 * 
 */
package org.gcube.data.tmf.api;

import java.util.List;

import org.gcube.data.tmf.api.exceptions.InvalidRequestException;
import org.w3c.dom.Element;

/**
 * Binds the plugin to data sources in response to client requests.
 * <p>
 * A binder provides the service with {@link Source} models of the bound data
 * sources, pre-configured with the information required by the service to
 * manage the sources on behalf of the plugin.
 * 
 * 
 * @author Fabio Simeoni
 * @see Source
 */
public interface SourceBinder {

	/**
	 * Binds one or more data sources in response to client requests, returning
	 * {@link Source} models of the bound sources to the service.
	 * <p>
	 * Implementations should validate requests and return {@link Source}s as
	 * soon as these can be identified from the requests. Sources should be
	 * instead initialised in {@link SourceLifecycle#init()}), if and when the
	 * service requires it. Doing otherwise may result in unnecessary work
	 * because the service may recognise that a {@link Source} instance with the
	 * same identifier has been previously bound by the plugin. In this case,
	 * the service discards the new instance and invokes
	 * {@link SourceLifecycle#reconfigure(Element)}) on the old instance.
	 * 
	 * @param request
	 *            the client request
	 * @throws InvalidRequestException
	 *             if the request is invalid
	 * @throws Exception
	 *             if operation fails for any other error
	 * @return the bound sources
	 * @see SourceLifecycle
	 */
	List<? extends Source> bind(Element request)
			throws InvalidRequestException, Exception;
}
