package org.gcube.data.analysis.statisticalmanager.experimentspace;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.data.analysis.statisticalmanager.ServiceContext;

public class ComputationFactoryContext extends GCUBEStatefulPortTypeContext {

    /** Singleton instance. */
    private static GCUBEStatefulPortTypeContext cache = new ComputationFactoryContext();

    /**Creates an instance, privately. */
    private ComputationFactoryContext(){}

    /** Returns the singleton context.
    /* @return the context.*/
    public static GCUBEStatefulPortTypeContext getContext() {return cache;}

    /** {@inheritDoc} */
    @Override
	public String getJNDIName() {return "gcube/data/analysis/statisticalmanager/statisticalmanagerfactory";}

    /** {@inheritDoc} */
    @Override
	public String getNamespace() {return "http://gcube-system.org/namespaces/data/analysis/statisticalmanager";}
    
    /** {@inheritDoc} */
    @Override
	public GCUBEServiceContext getServiceContext() {return ServiceContext.getContext();}

}
