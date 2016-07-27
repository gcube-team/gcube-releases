package org.acme.sample;

import static org.acme.sample.Utils.*;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;

public class StatefulContext extends GCUBEStatefulPortTypeContext {

    public static String FREQUENT_USER_LIMIT_JNDI_NAME = "frequentUserLimit";

	/** Single context instance, created eagerly */
    private static GCUBEStatefulPortTypeContext cache = new StatefulContext();
    
    /**Create an instance, privately */
    private StatefulContext(){}
	
	/** Returns singleton context.
	 * @return the context. */
    public static GCUBEStatefulPortTypeContext getContext() {return cache;}
    
    /** {@inheritDoc} **/
    public String getJNDIName() {return STATEFUL_NAME;}

    /** {@inheritDoc} **/
    public String getNamespace() {return NS;}

    /** {@inheritDoc} **/
    public GCUBEServiceContext getServiceContext() {return ServiceContext.getContext();}

    
    

        
}
