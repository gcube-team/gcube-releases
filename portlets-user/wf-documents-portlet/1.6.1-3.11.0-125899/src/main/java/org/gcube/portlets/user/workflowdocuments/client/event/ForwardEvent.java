package org.gcube.portlets.user.workflowdocuments.client.event;

import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.google.gwt.event.shared.GwtEvent;

public class ForwardEvent  extends GwtEvent<ForwardEventHandler> {
	public static Type<ForwardEventHandler> TYPE = new Type<ForwardEventHandler>();
	private final WorkflowDocument workflow;
	private final String toStepLabel;
	
	public ForwardEvent(WorkflowDocument workflow, String toStepLabel) {
		super();
		this.workflow = workflow;
		this.toStepLabel = toStepLabel;
	}

	public WorkflowDocument getWorkflow() {
		return workflow;
	}

	public String getToStepLabel() {
		return toStepLabel;
	}

	@Override
	public Type<ForwardEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ForwardEventHandler handler) {
		handler.onHasForwarded(this);
	}
}
