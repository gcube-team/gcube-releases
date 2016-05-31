package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.GeneralInfo;

import net.customware.gwt.dispatch.shared.Action;

public class SetGeneralInfo implements Action<SetGeneralInfoResult> {

	private GeneralInfo generalInfo;
	
	@SuppressWarnings("unused")
	private SetGeneralInfo() {
		// Serialization only
	}

	public SetGeneralInfo(GeneralInfo generalInfo) {
		super();
		this.generalInfo = generalInfo;
	}
	
	public GeneralInfo getGeneralInfo() {
		return generalInfo;
	}
	
}
