package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import org.gcube.portlets.admin.software_upload_wizard.shared.ImportSessionId;

import net.customware.gwt.dispatch.shared.Result;

public class CreateImportSessionResult implements Result {

	ImportSessionId id;
	
	@SuppressWarnings("unused")
	private CreateImportSessionResult() {
		//For serialization only
	}

	public CreateImportSessionResult(ImportSessionId id) {
		this.id = id;
	}

	public ImportSessionId getId() {
		return id;
	}
}
