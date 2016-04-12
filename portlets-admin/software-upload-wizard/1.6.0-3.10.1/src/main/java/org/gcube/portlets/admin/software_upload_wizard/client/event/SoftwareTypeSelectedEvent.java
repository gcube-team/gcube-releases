package org.gcube.portlets.admin.software_upload_wizard.client.event;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

import com.google.gwt.event.shared.GwtEvent;

public class SoftwareTypeSelectedEvent extends
		GwtEvent<SoftwareTypeSelectedEventHandler> {
	public static Type<SoftwareTypeSelectedEventHandler> TYPE = new Type<SoftwareTypeSelectedEventHandler>();

	SoftwareTypeCode softwareTypeCode;

	public SoftwareTypeSelectedEvent(SoftwareTypeCode code) {
		softwareTypeCode = code;
	}

	public SoftwareTypeCode getSoftwareTypeCode(){
		return softwareTypeCode;
	}
	
	@Override
	public Type<SoftwareTypeSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SoftwareTypeSelectedEventHandler handler) {
		handler.onSoftwareTypeSelected(this);
	}

}
