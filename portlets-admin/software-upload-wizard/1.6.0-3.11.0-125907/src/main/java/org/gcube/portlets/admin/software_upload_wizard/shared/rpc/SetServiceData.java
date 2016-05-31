package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;

public class SetServiceData implements Action<SetServiceDataResult> {

	ServiceData data;
	
	@SuppressWarnings("unused")
	private SetServiceData() {
		// Serialization only
	}

	public SetServiceData(ServiceData data) {
		super();
		this.data = data;
	}

	public ServiceData getData() {
		return data;
	}

}
