package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetStringData implements Action<GetStringDataResult> {

	private String key;
	
	@SuppressWarnings("unused")
	private GetStringData() {
		// Serialization only
	}

	public GetStringData(String key) {
		super();
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
	
}
