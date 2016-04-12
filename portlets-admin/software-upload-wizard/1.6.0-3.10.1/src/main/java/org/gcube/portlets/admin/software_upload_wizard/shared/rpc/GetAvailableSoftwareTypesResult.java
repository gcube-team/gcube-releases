package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.gcube.portlets.admin.software_upload_wizard.shared.softwaretypes.ISoftwareTypeInfo;

public class GetAvailableSoftwareTypesResult implements Result {
	
	ArrayList<ISoftwareTypeInfo> types;
	
	@SuppressWarnings("unused")
	private GetAvailableSoftwareTypesResult() {
	}

	public GetAvailableSoftwareTypesResult(ArrayList<ISoftwareTypeInfo> types) {
		super();
		this.types = types;
	}
	
	public ArrayList<ISoftwareTypeInfo> getTypes() {
		return types;
	}

}
