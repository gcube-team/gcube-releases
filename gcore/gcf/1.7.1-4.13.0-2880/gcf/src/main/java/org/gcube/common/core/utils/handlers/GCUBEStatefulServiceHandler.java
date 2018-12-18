package org.gcube.common.core.utils.handlers;

import java.util.ArrayList;
import java.util.List;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.client.RPDocument;

/**
 * Abstract specialisation of {@link GCUBEServiceHandler} to <em>stateful</em> service port-types
 * which follow WSRF's implied resource access pattern.
 * 
 * <p>
 * 
 * Subclasses must implement the abstract method {@link #findWSResources()}, to interact 
 * with WS-Resources produced by instances of the stateful port-type :<p>
 * 
 * @param <CLIENT> the type of the handled object. It must implement the {@link GCUBEServiceClient} interface.
 */
public abstract class GCUBEStatefulServiceHandler<CLIENT extends GCUBEServiceClient> extends GCUBEServiceHandler<CLIENT>{
	

	/**
	 * Creates an instance.
	 */
	public GCUBEStatefulServiceHandler() {}
	
	/**
	 * Creates an instance to act on or on behalf of a service client.
	 * @param client the client.
	 */
	public GCUBEStatefulServiceHandler(CLIENT client) {super(client);}
	
	/** {@inheritDoc} **/
	final protected List<EndpointReferenceType> findInstances() throws Exception {

		List<RPDocument> rpds = this.findWSResources();
		if (rpds == null || rpds.size()==0) {throw new NoQueryResultException();}
		List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();
		for (RPDocument rpd : rpds) eprs.add(rpd.getEndpoint());
		return eprs;
	}

	/**
	 * Discovers suitable WS-Resources of the target port-type.
	 * 
	 * @return the EPRs.
	 * @throws Exception if suitable instances cannot be found.
	 */
	protected abstract List<RPDocument> findWSResources() throws Exception;
	
	
	}
