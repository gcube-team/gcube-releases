package org.gcube.common.core.utils.handlers;

import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.scope.GCUBEScope;

/**
 * Basic implementation of the {@link GCUBEServiceClient} interface.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBEServiceClientImpl implements GCUBEServiceClient {

	/** Service map. */
	private Map<String,EndpointReferenceType> cache;
	
	/** Returns the service map. */
	public Map<String, EndpointReferenceType> getPortTypeMap() {return cache;}

	/** Sets the service map. */
	public void setPortTypeMap(Map<String, EndpointReferenceType> map) {this.cache=map;}
	
	/** {@inheritDoc} */
	public GCUBEScope getScope() {return null;}

}
