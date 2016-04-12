package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;

import net.customware.gwt.dispatch.shared.Result;

public class GetGeneralInfoResult implements Result {

	private GeneralInfo generalInfo;
	
	@SuppressWarnings("unused")
	private	GetGeneralInfoResult() {
		// Serialization only
	}

	public GetGeneralInfoResult(GeneralInfo generalInfo) {
		super();
		this.generalInfo = generalInfo;
	}
	
	public GeneralInfo getGeneralInfo() {
		return generalInfo;
	}
	
}
