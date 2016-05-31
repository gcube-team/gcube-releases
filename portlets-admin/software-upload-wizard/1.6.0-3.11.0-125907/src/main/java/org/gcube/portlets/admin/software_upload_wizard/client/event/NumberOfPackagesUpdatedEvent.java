package org.gcube.portlets.admin.software_upload_wizard.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NumberOfPackagesUpdatedEvent extends GwtEvent<NumberOfPackagesUpdatedEventHandler> {

	public static final Type<NumberOfPackagesUpdatedEventHandler> TYPE = new Type<NumberOfPackagesUpdatedEventHandler>();
	
	@Override
	public Type<NumberOfPackagesUpdatedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(NumberOfPackagesUpdatedEventHandler handler) {
		handler.onNumberOfPackagesUpdated(this);
	}

}
