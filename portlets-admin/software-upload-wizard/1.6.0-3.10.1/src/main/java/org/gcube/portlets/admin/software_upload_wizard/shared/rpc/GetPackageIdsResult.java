package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

public class GetPackageIdsResult implements Result {

	ArrayList<String> ids;
	
	@SuppressWarnings("unused")
	private GetPackageIdsResult() {
		//Serialization only
	}

	public GetPackageIdsResult(ArrayList<String> ids) {
		super();
		this.ids = ids;
	}
	
	public ArrayList<String> getIds() {
		return ids;
	}
	
}
