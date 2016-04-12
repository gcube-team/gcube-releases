package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile.ServiceData;

import net.customware.gwt.dispatch.shared.Result;

public class GetServiceDataResult implements Result {

	private ServiceData data;

	@SuppressWarnings("unused")
	private GetServiceDataResult() {
		// Serialization only
	}

	public GetServiceDataResult(ServiceData data) {
		this.data = data;
	}

	public ServiceData getData() {
		return data;
	}
}
