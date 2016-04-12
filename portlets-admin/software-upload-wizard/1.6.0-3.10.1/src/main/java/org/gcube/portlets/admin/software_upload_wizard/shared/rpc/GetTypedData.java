package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class GetTypedData<T> implements Action<GetTypedDataResult<T>> {

	private String key;
	
	@SuppressWarnings("unused")
	private GetTypedData() {
	}
	
	public GetTypedData(String key) {
		super();
		this.key = key;
	}	
	
	public String getKey() {
		return key;
	}
	
}
