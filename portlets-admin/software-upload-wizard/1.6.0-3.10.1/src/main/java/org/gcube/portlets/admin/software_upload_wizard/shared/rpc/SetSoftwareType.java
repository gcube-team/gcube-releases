package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.SoftwareTypeCode;

public class SetSoftwareType implements Action<SetSoftwareTypeResult> {

	SoftwareTypeCode code;
	
	@SuppressWarnings("unused")
	private SetSoftwareType() {
		// Serialization only
	}

	public SetSoftwareType(SoftwareTypeCode code) {
		super();
		this.code = code;
	}
	
	public SoftwareTypeCode getCode() {
		return code;
	}
	
}
