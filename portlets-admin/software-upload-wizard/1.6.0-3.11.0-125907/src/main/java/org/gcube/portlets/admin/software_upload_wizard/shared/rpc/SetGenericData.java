package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class SetGenericData implements Action<SetGenericDataResult> {

	private String key;
	private Object value;

	@SuppressWarnings("unused")
	private SetGenericData() {
		// Serialization only
	}

	public SetGenericData(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public String getKey() {
		return key;
	}
}
