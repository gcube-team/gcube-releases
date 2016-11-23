package org.gcube.datatransfer.scheduler.impl.context;

import static org.gcube.datatransfer.scheduler.impl.constants.Constants.*;

import org.gcube.common.core.contexts.GCUBEServiceContext;
import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.resources.GCUBEResource.ResourceTopic;
import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;
import org.gcube.datatransfer.scheduler.db.model.DataStorage;
import org.gcube.datatransfer.scheduler.impl.state.SchedulerRIResourceConsumer;
import org.gcube.datatransfer.scheduler.is.ISManager;

public class SchedulerContext extends GCUBEStatefulPortTypeContext {

    public static String FREQUENT_USER_LIMIT_JNDI_NAME = "frequentUserLimit";

	/** Single context instance, created eagerly */
    private static GCUBEStatefulPortTypeContext cache = new SchedulerContext();
    
    /**Create an instance, privately */
    private SchedulerContext(){}
	
	/** Returns singleton context.
	 * @return the context. */
    public static GCUBEStatefulPortTypeContext getContext() {return cache;}
    
    /** {@inheritDoc} **/
    public String getJNDIName() {return SCHEDULER_NAME;}

    /** {@inheritDoc} **/
    public String getNamespace() {return NS;}

    /** {@inheritDoc} **/
    public GCUBEServiceContext getServiceContext() {return ServiceContext.getContext();}

    

        
}
