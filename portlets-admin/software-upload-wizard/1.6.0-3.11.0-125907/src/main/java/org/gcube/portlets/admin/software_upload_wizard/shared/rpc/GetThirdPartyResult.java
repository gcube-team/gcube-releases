package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetThirdPartyResult implements Result {

	private boolean isThirdParty;

	@SuppressWarnings("unused")
	private GetThirdPartyResult() {
		// Serialization only
	}

	public GetThirdPartyResult(boolean isThirdParty) {
		super();
		this.isThirdParty = isThirdParty;
	}
	
	public boolean isThirdParty() {
		return isThirdParty;
	}
	
}
