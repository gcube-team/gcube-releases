package org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes;

import java.io.Serializable;

public interface ISoftwareTypeInfo extends Serializable{
	
	public String getDescription();
	
	public SoftwareTypeCode getCode();
	
	public String getName();
	
}
