package org.gcube.portlets.admin.software_upload_wizard.shared.softwareprofile;

import java.io.Serializable;

public class PortType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8872863166790014140L;
	
	private String name;
	
	@SuppressWarnings("unused")
	private PortType() {
		// Serialization only
	}

	public PortType(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
