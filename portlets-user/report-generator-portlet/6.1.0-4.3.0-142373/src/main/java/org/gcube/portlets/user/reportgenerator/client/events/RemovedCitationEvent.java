package org.gcube.portlets.user.reportgenerator.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class RemovedCitationEvent extends GwtEvent<RemovedCitationEventHandler>{
	public static Type<RemovedCitationEventHandler> TYPE = new Type<RemovedCitationEventHandler>();
	private final String citekey;

	public RemovedCitationEvent(String citekey) {
		this.citekey = citekey;
	}

	public String getCitekey() {
		return citekey;
	}

	@Override
	public Type<RemovedCitationEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemovedCitationEventHandler handler) {
		handler.onRemovedCitation(this);
	}
}
