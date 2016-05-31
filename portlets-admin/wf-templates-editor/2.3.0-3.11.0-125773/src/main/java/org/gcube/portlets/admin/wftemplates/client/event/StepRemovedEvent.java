package org.gcube.portlets.admin.wftemplates.client.event;

import org.gcube.portlets.admin.wftemplates.client.view.WfStep;

import com.google.gwt.event.shared.GwtEvent;

public class StepRemovedEvent extends GwtEvent<StepRemovedEventHandler>{
	public static Type<StepRemovedEventHandler> TYPE = new Type<StepRemovedEventHandler>();
	private final WfStep removedStep;
	
	public StepRemovedEvent(WfStep removedStep) {
		super();
		this.removedStep = removedStep;
	}

	public WfStep getRemovedStep() {
		return removedStep;
	}

	@Override
	protected void dispatch(StepRemovedEventHandler handler) {
		handler.onStepRemoved(this);
	}

	@Override
	public Type<StepRemovedEventHandler> getAssociatedType() {
		return TYPE;
	}

}
