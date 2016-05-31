package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;

import net.customware.gwt.dispatch.shared.Result;

public class SetServiceDataResult implements Result {

	ServiceData data;
	
	@SuppressWarnings("unused")
	private SetServiceDataResult() {
		// Serialization only
	}

	public SetServiceDataResult(ServiceData data) {
		super();
		this.data = data;
	}

	public ServiceData getData() {
		return data;
	}
}
