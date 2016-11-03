package org.gcube.datatransfer.scheduler.impl.context;

import static org.gcube.datatransfer.scheduler.impl.constants.Constants.*;

import org.gcube.common.core.contexts.GCUBEPortTypeContext;
import org.gcube.common.core.contexts.GCUBEServiceContext;

public class ManagementContext extends GCUBEPortTypeContext {

	
	/** Single context instance, created eagerly */
	private static ManagementContext cache = new ManagementContext();
	
	private ManagementContext(){}
	
	/** Returns cached instance */
	public static ManagementContext getContext() {return cache;}
	
	/**{@inheritDoc}*/
	public String getJNDIName() {return MANAGEMENT_NAME;}

	/** {@inheritDoc}*/
	public String getNamespace() {return NS;}

	/** {@inheritDoc}*/
	public GCUBEServiceContext getServiceContext() {return ServiceContext.getContext();}

}
