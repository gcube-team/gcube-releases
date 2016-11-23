package org.gcube.informationsystem.collector.impl.contexts;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.State;

/**
 * 
 * Information Collector's service context
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class ICServiceContext extends GCUBEServiceContext {


    /** singleton instance of IC context*/
    protected static final ICServiceContext cache = new ICServiceContext();

    private ICServiceContext() {}

    /**
     * @return the service context
     */
    public static ICServiceContext getContext() {
	return cache;
    }

    /** {@inheritDoc} */
    @Override
    protected String getJNDIName() {
	return "gcube/informationsystem/collector";
    }

    
    /** {@inheritDoc} */
    @Override
    protected void onInitialisation() throws Exception {
	State.initialize();
    }

    /** {@inheritDoc} */
    @Override
    protected void onReady() throws Exception {
	//State.initialize();
    }

    /** {@inheritDoc} */
    @Override
    protected void onShutdown() throws Exception {
	State.dispose();
    }

}
