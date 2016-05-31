package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetGenericData implements Action<GetGenericDataResult> {
	
	private String key;

	@SuppressWarnings("unused")
	private GetGenericData() {
		// Serialization only
	}
	
	public GetGenericData(String key) {
		super();
		this.key = key;
	}

	public String getKey() {
		return key;
	}


}
