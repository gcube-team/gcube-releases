package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class SetThirdPartyResult implements Result {

	private boolean thirdParty;
	
	@SuppressWarnings("unused")
	private SetThirdPartyResult() {
		// Serialization only
	}

	public SetThirdPartyResult(boolean thirdParty) {
		super();
		this.thirdParty = thirdParty;
	}
	
	public boolean isThirdParty() {
		return thirdParty;
	}
	
}
