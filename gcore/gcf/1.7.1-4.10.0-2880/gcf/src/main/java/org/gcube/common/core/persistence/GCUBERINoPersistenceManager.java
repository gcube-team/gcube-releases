package org.gcube.common.core.persistence;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.utils.proxies.AccessControlProxyContext.Restricted;

/**
 * Simulated implementation of{@link GCUBERIPersistenceManager}.
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public class GCUBERINoPersistenceManager extends GCUBERIPersistenceManager {

	/** 
	 * Creates a new instance for a given service and from a given configuration profile.
	 * @param ctxt the context of the service.
	 * @param profile the configuration profile.
	 */
	public GCUBERINoPersistenceManager(GCUBEServiceContext ctxt, GCUBERIPersistenceManagerProfile profile){super(ctxt,profile);}
	/**{@inheritDoc}*/
	@Restricted public synchronized void recover() throws Exception {}
	/**{@inheritDoc}*/
	@Restricted public synchronized void commit() throws Exception {}
	/**{@inheritDoc}*/
	@Override protected void commitState() throws Exception {}
	/**{@inheritDoc}*/
	@Override protected void recoverState() throws StateNotFoundException,Exception {}
	
}
