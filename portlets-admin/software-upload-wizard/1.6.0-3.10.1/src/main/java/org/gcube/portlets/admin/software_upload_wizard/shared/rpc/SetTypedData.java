package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class SetTypedData<T> implements Action<SetTypedDataResult<T>> {

	private String key;
	private T value;
	
	public SetTypedData(String key, T value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public T getValue() {
		return value;
	}
	
}
