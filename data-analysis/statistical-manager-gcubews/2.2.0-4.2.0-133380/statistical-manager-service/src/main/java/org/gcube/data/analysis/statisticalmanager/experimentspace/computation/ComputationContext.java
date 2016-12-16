package org.gcube.data.analysis.statisticalmanager.experimentspace.computation;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;

public class ComputationContext extends GCUBEStatefulPortTypeContext {

	/** Singleton instance. */
    private static GCUBEStatefulPortTypeContext cache = new ComputationContext();

    /**Creates an instance, privately. */
    private ComputationContext(){}

    /** Returns the singleton context.
    /* @return the context.*/
    public static GCUBEStatefulPortTypeContext getContext() {return cache;}

    /** {@inheritDoc} */
    @Override
	public String getJNDIName() {return "gcube/data/analysis/statisticalmanager/statisticalmanagerservice";}

    /** {@inheritDoc} */
    @Override
	public String getNamespace() {return "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";}
    
    /** {@inheritDoc} */
    @Override
	public GCUBEServiceContext getServiceContext() {return ServiceContext.getContext();}
}
