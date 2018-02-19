package org.gcube.portlets.user.reportgenerator.client.events;

import org.gcube.portlets.user.reportgenerator.client.targets.ReportTextArea;

import com.google.gwt.event.shared.GwtEvent;

public class RemovedUserCommentEvent extends GwtEvent<RemovedUserCommentEventHandler>{
	public static Type<RemovedUserCommentEventHandler> TYPE = new Type<RemovedUserCommentEventHandler>();
	private final ReportTextArea source;
	

	public RemovedUserCommentEvent(ReportTextArea source) {
		super();
		this.source = source;
	}
	
	public ReportTextArea getSourceComponent() {
		return source;
	}

	@Override
	public Type<RemovedUserCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemovedUserCommentEventHandler handler) {
		handler.onRemovedComment(this);
	}
}
