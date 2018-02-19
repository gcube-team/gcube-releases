package org.acme.sample;

import static org.acme.sample.Utils.*;

import org.gcube.common.core.contexts.GCUBEServiceContext;

public class ServiceContext extends GCUBEServiceContext {


	/** Single context instance, created eagerly */
	private static ServiceContext cache = new ServiceContext();
	
	/** Returns cached instance */
	public static ServiceContext getContext() {return cache;}
	
	/** Prevents accidental creation of more instances */
	private ServiceContext(){};
		
	/** {@inheritDoc} */
	protected String getJNDIName() {return NAME;}
	
	
	
	
}
