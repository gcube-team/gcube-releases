package org.gcube.portlets.user.workflowdocuments.client.event;

import com.google.gwt.event.shared.GwtEvent;
/**
 * <code> AddCommentEvent </code>  is the event fired in case of new comment is added
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class SelectRowEvent extends GwtEvent<SelectRowEventHandler> {
	public static Type<SelectRowEventHandler> TYPE = new Type<SelectRowEventHandler>();
	private final String workflowid;

	public SelectRowEvent(String workflowid) {
		this.workflowid = workflowid;
	}
		
	public String getWorkflowid() {
		return workflowid;
	}

	@Override
	public Type<SelectRowEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectRowEventHandler handler) {
		handler.onRowSelect(this);
	}
}
