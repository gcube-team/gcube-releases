package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Action;

public class SetStringData implements Action<SetStringDataResult> {

	private String key;
	private String value;

	@SuppressWarnings("unused")
	private SetStringData() {
		// Serialization only
	}

	public SetStringData(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

}
