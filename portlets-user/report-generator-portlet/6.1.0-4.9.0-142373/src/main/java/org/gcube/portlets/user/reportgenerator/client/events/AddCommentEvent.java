package org.gcube.portlets.user.reportgenerator.client.events;

import org.gcube.portlets.user.reportgenerator.client.targets.ReportTextArea;

import com.google.gwt.event.shared.GwtEvent;

public class AddCommentEvent extends GwtEvent<AddCommentEventHandler>{
	public static Type<AddCommentEventHandler> TYPE = new Type<AddCommentEventHandler>();
	private final ReportTextArea source;
	private final String comment;
	private final int areaHeight;

	

	public AddCommentEvent(ReportTextArea sourceComponent, String comment, int areaHeight) {
		super();
		this.source = sourceComponent;
		this.comment = comment;
		this.areaHeight = areaHeight;
	}

	public int getAreaHeight() {
		return areaHeight;
	}

	public ReportTextArea getSourceComponent() {
		return source;
	}

	public String getComment() {
		return comment;
	}

	@Override
	public Type<AddCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddCommentEventHandler handler) {
		handler.onAddComment(this);
	}
}
