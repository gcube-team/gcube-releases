package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import net.customware.gwt.dispatch.shared.Result;

import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;

public class GetMiscFilesResult implements Result {

	private ArrayList<Deliverable> files;
	
	@SuppressWarnings("unused")
	private GetMiscFilesResult() {
		// Serialization only
	}

	public GetMiscFilesResult(ArrayList<Deliverable> files) {
		super();
		this.files = files;
	}
	
	public ArrayList<Deliverable> getFiles() {
		return files;
	}
	
}
