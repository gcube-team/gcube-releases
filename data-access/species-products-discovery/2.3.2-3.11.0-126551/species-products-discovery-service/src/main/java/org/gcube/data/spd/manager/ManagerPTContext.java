package org.gcube.data.spd.manager;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.data.spd.Constants;
import org.gcube.data.spd.context.ServiceContext;

public class ManagerPTContext extends GCUBEStatefulPortTypeContext{

	/** Singleton instance. */
	protected static ManagerPTContext cache = new ManagerPTContext();

	/** Creates an instance . */
	private ManagerPTContext(){}
	
	/** Returns a context instance.
	 * @return the context.*/
	public static ManagerPTContext getContext() {return cache;}
	
	@Override
	public String getJNDIName() {
		return Constants.MANAGER_PT_NAME;
	}

	@Override
	public String getNamespace() {
		return Constants.NS;
	}

	@Override
	public GCUBEServiceContext getServiceContext() {
		return ServiceContext.getContext();
	}

}
