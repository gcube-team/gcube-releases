package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetThirdPartyCapabilityResult implements Result {
	
	boolean allowsThirdParty;
	
	@SuppressWarnings("unused")
	private GetThirdPartyCapabilityResult() {
		// Serialization only
	}

	public GetThirdPartyCapabilityResult(boolean allowsThirdParty) {
		super();
		this.allowsThirdParty = allowsThirdParty;
	}
	
	public boolean allowsThirdParty() {
		return allowsThirdParty;
	}

}
