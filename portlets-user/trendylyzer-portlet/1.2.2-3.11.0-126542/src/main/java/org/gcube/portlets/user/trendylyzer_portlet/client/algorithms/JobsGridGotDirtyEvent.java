package org.gcube.portlets.user.trendylyzer_portlet.client.algorithms;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HasHandlers;

public class JobsGridGotDirtyEvent extends GwtEvent<JobsGridGotDirtyEvent.JobsGridGotDirtyHandler> {

	public static Type<JobsGridGotDirtyHandler> TYPE = new Type<JobsGridGotDirtyHandler>();

	public interface JobsGridGotDirtyHandler extends EventHandler {
		void onJobsGridGotDirty(JobsGridGotDirtyEvent event);
	}

	public JobsGridGotDirtyEvent() {
	}

	@Override
	protected void dispatch(JobsGridGotDirtyHandler handler) {
		handler.onJobsGridGotDirty(this);
	}

	@Override
	public Type<JobsGridGotDirtyHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<JobsGridGotDirtyHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source) {
		source.fireEvent(new JobsGridGotDirtyEvent());
	}
}
