package org.acme.sample;

import static org.acme.sample.Utils.*;

import org.gcube.common.core.contexts.GCUBEPortTypeContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;

public class StatelessContext extends GCUBEPortTypeContext {

	
	/** Single context instance, created eagerly */
	private static StatelessContext cache = new StatelessContext();
	
	private StatelessContext(){}
	
	/** Returns cached instance */
	public static StatelessContext getContext() {return cache;}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {return STATELESS_NAME;}

	/** {@inheritDoc}*/
	public String getNamespace() {return NS;}

	/** {@inheritDoc}*/
	public GCUBEServiceContext getServiceContext() {return ServiceContext.getContext();}

}
