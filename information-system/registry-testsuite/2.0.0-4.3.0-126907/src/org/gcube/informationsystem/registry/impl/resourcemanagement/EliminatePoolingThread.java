package org.gcube.informationsystem.registry.impl.resourcemanagement;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.registry.impl.contexts.ProfileContext;
import org.gcube.informationsystem.registry.impl.contexts.ServiceContext;

/**
 * 
 * Manages the asynchronous deletion of temporary resources
 * 
 * @author Lucio Lelii, Manuele Simi (ISTI-CNR)
 * 
 */
public class EliminatePoolingThread extends Thread {

	private final long sleepTime =  (Long) ServiceContext.getContext().getProperty("temporaryResourceSweeperIntervalInMs");

	private List<Pair> stack = Collections.synchronizedList(new LinkedList<Pair>());

	private static GCUBELog logger = new GCUBELog(EliminatePoolingThread.class);

	public void run() {

		while (true) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
			
			try {				
				synchronized (stack) {
					int numRes = stack.size();
					LinkedList<Pair> undeletedResources = this.checkResources();
					logger.debug("cannot destroy " + undeletedResources.size() + " resources, retrying later");
					logger.debug("destroyed " + (numRes - undeletedResources.size()) + " resources ");
					stack.addAll(undeletedResources);
				} // end synchronized block

			} catch (Exception e) {
				logger.error("Cannot continue with thread Excecution " + e);
				break;
			}
		}
	}

	public synchronized List<Pair> getStack() {
		return this.stack;
	}
	
	/**
	 * Checks and deletes expired temporary resources
	 * @return the list of still living resources
	 */
	private LinkedList<Pair> checkResources() {
		
		LinkedList<Pair> tmpStack = new LinkedList<Pair>();
		long timestamp = System.currentTimeMillis();
		
		while (stack.size() > 0) {
			Pair c = stack.remove(stack.size() - 1);
			logger.trace("checking resource for deletion " + c.resource.getID());
			logger.trace("timenstamp now: " + timestamp + ", resource lifetime: " + c.lifetime);
			if (timestamp >= c.lifetime) {
				try {
					logger.debug("temporary resource " + c.resource.getID() + " is going to be deleted");
					ProfileContext.getContext().getWSHome().remove(c.resource.getID());
				} catch (Exception e) {
					logger.error(e);
					tmpStack.offer(c);// re-insert the resource in the list
				}
			} else {
				tmpStack.offer(c); // re-insert the resource in the list
			}
		}
		return tmpStack;
	}

}
