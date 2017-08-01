package org.gcube.common.core.informationsystem.client;

import java.util.Calendar;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.state.GCUBEWSResourceKey;

/**
 * A specialisation of {@link XMLResult} to Resource Property Document of WS-Resources.
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (CNR)
 *
 */
public interface RPDocument extends XMLResult {

	/**
	 * Returns the WS-Resource endpoint.
	 * @return the endpoint.
	 */
	public EndpointReferenceType getEndpoint(); 

	/**
	 * Returns the WS-Resource enkeydpoint.
	 * @return the key.
	 */
	public GCUBEWSResourceKey getKey(); 
	
	/**
	 * Returns the identifier of the service of the WS-Resource.
	 * @return the identifier.
	 */
	public String getServiceID();
	
	/**
	 * Returns the name of the service of origin of the WS-Resource.
	 * @return the name.
	 */
	public String getServiceName();
	
	/**
	 * Returns the class of the service of origin of the WS-Resource.
	 * @return the class.
	 */
	public String getServiceClass();
	
	/**
	 * Returns the identifier of the running instance of origin of the WS-Resource.
	 * @return the identifier.
	 */
	public String getRIID();
	
	/**
	 * Returns the identifier of the gHN of origin of the WS-Resource.
	 * @return the identifier.
	 */
	public String getGHNID();
	
	/**
	 * Returns the scopes of the WS-Resource.
	 * @return the scopes.
	 */
	public List<GCUBEScope> getScope();
	
	/**
	 * Returns the termination time of the WS-Resource.
	 * @return the termination time.
	 */
	public Calendar getTerminationTime();
}
