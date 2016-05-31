package org.gcube.portlets.admin.software_upload_wizard.client.event;

import org.gcube.portlets.admin.software_upload_wizard.client.view.card.WizardCard;

import com.google.gwt.event.shared.GwtEvent;

public class GoAheadEvent extends
		GwtEvent<GoAheadEventHandler> {

	public static final Type<GoAheadEventHandler> TYPE = new Type<GoAheadEventHandler>();

	private WizardCard relativeCard;

	public GoAheadEvent(WizardCard relativeCard) {
		this.relativeCard = relativeCard;
	}

	public WizardCard getRelativeCard() {
		return relativeCard;
	}

	@Override
	public GwtEvent.Type<GoAheadEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GoAheadEventHandler handler) {
		handler.onNextButtonPressed(this);
	}

}
