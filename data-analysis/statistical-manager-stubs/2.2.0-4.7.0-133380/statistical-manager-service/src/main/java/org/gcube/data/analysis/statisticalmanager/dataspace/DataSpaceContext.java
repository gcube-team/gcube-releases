package org.gcube.data.analysis.statisticalmanager.dataspace;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;

public class DataSpaceContext extends GCUBEStatefulPortTypeContext {

	 /** Singleton instance. */
    private static GCUBEStatefulPortTypeContext cache = new DataSpaceContext();

    /**Creates an instance, privately. */
    private DataSpaceContext(){}
    
    /** Returns the singleton context.
    /* @return the context.*/
    public static GCUBEStatefulPortTypeContext getContext() {return cache;}
	
	@Override
	public String getJNDIName() {return "gcube/data/analysis/statisticalmanager/statisticalmanagerdataspace";}

	@Override
	public String getNamespace() {return "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";}

	@Override
	public GCUBEServiceContext getServiceContext() { return ServiceContext.getContext();}

	
	
}
