package org.gcube.portlets.admin.software_upload_wizard.client.event;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;

import com.google.gwt.event.shared.GwtEvent;

public class GoBackEvent extends
		GwtEvent<GoBackEventHandler> {

	public static final Type<GoBackEventHandler> TYPE = new Type<GoBackEventHandler>();
	
	private WizardCard relativeCard;
	
	public GoBackEvent(WizardCard relativeCard) {
		this.relativeCard = relativeCard;
	}
	
	public WizardCard getRelativeCard(){
		return this.relativeCard;
	}

	@Override
	public Type<GoBackEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GoBackEventHandler handler) {
		handler.onBackButtonPressed(this);

	}

}
