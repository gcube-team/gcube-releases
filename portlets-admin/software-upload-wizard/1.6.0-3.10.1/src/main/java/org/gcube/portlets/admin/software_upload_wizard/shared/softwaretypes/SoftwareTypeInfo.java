package org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes;


public class SoftwareTypeInfo implements ISoftwareTypeInfo{

	private SoftwareTypeCode code;
	private String name;
	private String description;
	
	@SuppressWarnings("unused")
	private SoftwareTypeInfo() {
		// Serialization only
	}
	
	public SoftwareTypeInfo(SoftwareTypeCode code, String name,
			String description) {
		this.code = code;
		this.name = name;
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public SoftwareTypeCode getCode() {
		return code;
	}

	@Override
	public String getName() {
		return name;
	}

}
