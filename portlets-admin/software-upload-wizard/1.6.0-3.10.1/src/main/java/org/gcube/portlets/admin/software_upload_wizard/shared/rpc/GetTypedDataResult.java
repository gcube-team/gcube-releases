package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetTypedDataResult<T> implements Result {

	private T value;
	
	@SuppressWarnings("unused")
	private GetTypedDataResult() {
		// Serialization only
	}

	public GetTypedDataResult(T value) {
		super();
		this.value = value;
	}

	public T getValue() {
		return value;
	}
	
}
