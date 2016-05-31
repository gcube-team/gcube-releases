package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetStringDataResult implements Result {

	private String value;
	
	@SuppressWarnings("unused")
	private GetStringDataResult() {
		// Serialization only
	}

	public GetStringDataResult(String value) {
		super();
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
