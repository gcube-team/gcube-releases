package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class SetThirdParty implements Action<SetThirdPartyResult> {

	private boolean thirdParty;
	
	@SuppressWarnings("unused")
	private SetThirdParty() {
		// Serialization only
	}

	public SetThirdParty(boolean thirdParty) {
		super();
		this.thirdParty = thirdParty;
	}
	
	public boolean isThirdParty() {
		return thirdParty;
	}
	
}
