package org.gcube.portlets.user.trendylyzer_portlet.client.results;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HasHandlers;

public class ResubmitJobEvent extends
		GwtEvent<ResubmitJobEvent.ResubmitJobHandler> {

	public static Type<ResubmitJobHandler> TYPE = new Type<ResubmitJobHandler>();
	private JobItem jobItem;

	public interface ResubmitJobHandler extends EventHandler {
		void onResubmitJob(ResubmitJobEvent event);
	}

	public ResubmitJobEvent(JobItem jobItem) {
		this.jobItem = jobItem;
	}

	public JobItem getJobItem() {
		return jobItem;
	}

	@Override
	protected void dispatch(ResubmitJobHandler handler) {
		handler.onResubmitJob(this);
	}

	@Override
	public Type<ResubmitJobHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ResubmitJobHandler> getType() {
		return TYPE;
	}

	public static void fire(HasHandlers source, JobItem jobItem) {
		source.fireEvent(new ResubmitJobEvent(jobItem));
	}
}
