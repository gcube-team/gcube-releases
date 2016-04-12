package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

import net.customware.gwt.dispatch.shared.Result;

public class SetSoftwareTypeResult implements Result {

	SoftwareTypeCode code;
	
	@SuppressWarnings("unused")
	private SetSoftwareTypeResult() {
		// For serialization only
	}

	public SetSoftwareTypeResult(SoftwareTypeCode code) {
		super();
		this.code = code;
	}
	
	public SoftwareTypeCode getCode() {
		return code;
	}
	
}
