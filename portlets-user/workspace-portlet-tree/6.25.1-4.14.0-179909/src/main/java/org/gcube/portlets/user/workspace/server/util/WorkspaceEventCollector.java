///**
// * 
// */
//package org.gcube.portlets.user.workspace.server.util;
//
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;
//
//import org.apache.log4j.Logger;
//import org.gcube.common.homelibrary.home.workspace.events.WorkspaceEvent;
//import org.gcube.common.homelibrary.home.workspace.events.WorkspaceListener;
//
///**
// * @author Federico De Faveri defaveriAtisti.cnr.it
// *
// */
//public class WorkspaceEventCollector implements WorkspaceListener {
//	
//	protected static Logger logger = Logger.getLogger(WorkspaceEventCollector.class);
//	
//	protected BlockingQueue<WorkspaceEvent> eventsQueue = new LinkedBlockingQueue<WorkspaceEvent>();
//
//	/**
//	 * @param logger
//	 */
//	public WorkspaceEventCollector() {
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	public void workspaceEvent(WorkspaceEvent event) {
//		try {
//			logger.trace("EventCollector workspaceEvent"+event);
//			eventsQueue.put(event);
//		} catch (InterruptedException e) {
//			logger.warn("Error adding a new event", e);
//		}		
//	}
//	
//	public WorkspaceEvent getEvent() throws InterruptedException
//	{
//		logger.trace("getEvent");
//		return eventsQueue.take();
//	}
//
//}
