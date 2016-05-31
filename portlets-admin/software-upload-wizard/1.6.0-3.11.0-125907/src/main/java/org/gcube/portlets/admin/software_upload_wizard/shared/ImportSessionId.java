package org.gcube.portlets.admin.software_upload_wizard.shared;

import java.io.Serializable;

public class ImportSessionId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4132580920544395632L;
	private String id;

	public ImportSessionId() {
	}

	public ImportSessionId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return id;
	}

}
