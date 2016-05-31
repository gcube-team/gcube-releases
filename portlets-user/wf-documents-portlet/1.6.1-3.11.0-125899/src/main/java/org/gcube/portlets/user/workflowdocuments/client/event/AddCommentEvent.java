package org.gcube.portlets.user.workflowdocuments.client.event;

import com.google.gwt.event.shared.GwtEvent;
/**
 * <code> AddCommentEvent </code>  is the event fired in case of new comment is added
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class AddCommentEvent extends GwtEvent<AddCommentEventHandler> {
	public static Type<AddCommentEventHandler> TYPE = new Type<AddCommentEventHandler>();
	private final String workflowid;
	private final String comment;

	public AddCommentEvent(String workflowid, String comment) {
		this.workflowid = workflowid;
		this.comment = comment;
	}
		
	public String getWorkflowid() {
		return workflowid;
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
		handler.onAddComent(this);
	}
}
