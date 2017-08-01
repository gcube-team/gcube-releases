package org.gcube.common.core.contexts.ghn;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.contexts.GHNContext.Status;
import org.gcube.common.core.utils.handlers.GCUBEHandler;
import org.gcube.common.core.utils.handlers.GCUBEScheduledHandler;

/**Schedules updates to the gHN profile.
 * @author Fabio Simeoni (University of Strathclyde)*/
public class Scheduler extends GCUBEScheduledHandler<GHNContext> {

	/**
	 * Creates an instance.
	 * @param ctxt the {@link GHNContext}.
	 * @param interval the schedule interval.
	 * @param mode the scheduling mode.
	 */
	public Scheduler(GHNContext ctxt, long interval, Mode mode) {super(interval, mode,new Updater(ctxt));}
	
	/**{@inheritDoc}*/
	protected boolean repeat(Exception exception, int exceptionCount)  {
			if (exception!=null) {
				logger.warn("error during scheduled building of GHN resource (attempt "+exceptionCount+" out of "+GHNContext.GHN_UPDATE_ATTEMPTS+")",exception);
				if (exceptionCount>=GHNContext.GHN_UPDATE_ATTEMPTS) {
					logger.error("giving up GHN building, this GHN is shutting down");
					((Updater) getScheduled()).getHandled().setStatus(Status.DOWN);//TODO: down or shutdown?? clarify relationship, should probably be latter
					return false;
				}
			}
			return true;
		}
	
	/** Updates the profile.*/
	static class Updater extends GCUBEHandler<GHNContext> {
		public Updater(GHNContext ctxt){super(ctxt);}
		public void run() throws Exception {Builder.updateGHNResource(getHandled());getHandled().setStatus(Status.UPDATED);}
	}	
	
}
