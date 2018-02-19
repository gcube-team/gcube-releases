package org.gcube.data.tml.proxies;

import java.util.List;

import org.gcube.common.clients.exceptions.DiscoveryException;
import org.gcube.common.clients.exceptions.InvalidRequestException;
import org.gcube.common.clients.exceptions.ServiceException;

/**
 * An interface over remote T-Binder endpoints.
 * 
 * <p>
 * T-Binder endpoints bind given data sources to T-Reader and T-Writer endpoints, which
 * give access to the bound sources under a tree-based model. 
 * 
 * <p>
 *  
 * @author Fabio Simeoni
 * 
 * @see TReader
 * @see TWriter
 *
 */
public interface TBinder {

	/**
	 * Binds one ore more data sources to T-Reader and/or T-Writer endpoints.
	 * @param parameters the binding parameters
	 * @return the bindings
	 * 
	 * @throws DiscoveryException if the proxy is created in discovery mode but no service endpoints can be discovered
	 * @throws InvalidRequestException if the input parameters are invalid
	 * @throws ServiceException if the call fails for any other error
	 */
	List<Binding> bind(BindRequest parameters);
}