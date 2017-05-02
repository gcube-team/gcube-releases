package org.gcube.informationsystem.registry.impl.resourcemanagement;

import java.util.List;
import org.gcube.common.core.informationsystem.notifier.ISNotifier;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.contexts.FactoryContext;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;
import org.globus.wsrf.Topic;

/**
 * Registration Thread class
 * 
 * @author Andrea Manzi (ISTI-CNR)
 * 
 * 
 */
public class RegistrationThread implements Runnable {

    private static GCUBELog logger = new GCUBELog(RegistrationThread.class);

    private List<? extends Topic> topics;  

    /**
     * The constructor
     * 
     * @param qname
     *            an Array list of RP qname
     * @throws Exception
     *             Exception
     */
    public RegistrationThread(List<? extends Topic> topics) throws Exception {
	this.topics = topics;
    }

    
    public void run() {

	while (true) {
	    try {
		// This is the time interval
		Thread.sleep(10000);
	    } catch (InterruptedException e) {}
	    try {

		ISNotifier notifier = GHNContext.getImplementation(ISNotifier.class);
		notifier.registerISNotification(FactoryContext.getContext().getEPR(), topics, ServiceContext.getContext(),
			ServiceContext.getContext().getInstance().getScopes().values().toArray(new GCUBEScope[0]));
		return;
	    } catch (Exception e) {
		logger.error("Error starting registration: Retrying in 10 seconds");
	    }
	}

    }

}
