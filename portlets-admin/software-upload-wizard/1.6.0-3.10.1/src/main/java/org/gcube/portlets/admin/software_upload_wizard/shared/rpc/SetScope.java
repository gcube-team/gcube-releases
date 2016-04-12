package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class SetScope implements Action<SetScopeResult> {
	
	String scope;
	
	@SuppressWarnings("unused")
	private SetScope() {
		// Serialization only
	}
	
	public SetScope(String scope) {
		super();
		this.scope = scope;
	}

	public String getScope() {
		return scope;
	}

}
