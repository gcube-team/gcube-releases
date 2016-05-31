package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetGenericDataResult implements Result {

	private Object value;

	@SuppressWarnings("unused")
	private GetGenericDataResult() {
		// Serialization only
	}

	public GetGenericDataResult(Object value) {
		super();
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

}
