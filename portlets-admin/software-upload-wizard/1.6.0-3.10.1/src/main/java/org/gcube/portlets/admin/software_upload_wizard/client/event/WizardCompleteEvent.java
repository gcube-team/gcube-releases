package org.gcube.portlets.admin.software_upload_wizard.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class WizardCompleteEvent extends GwtEvent<WizardCompleteEventHandler> {

	public static final Type<WizardCompleteEventHandler> TYPE = new Type<WizardCompleteEventHandler>();
	
	@Override
	public Type<WizardCompleteEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(WizardCompleteEventHandler handler) {
		handler.onWizardCompleted(this);
	}

}
