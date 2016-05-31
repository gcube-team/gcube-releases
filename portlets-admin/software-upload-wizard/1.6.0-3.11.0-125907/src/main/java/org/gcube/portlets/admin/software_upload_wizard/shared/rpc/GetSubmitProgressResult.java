package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

import org.gcube.portlets.admin.software_upload_wizard.shared.IOperationProgress;

public class GetSubmitProgressResult implements Result {

private IOperationProgress progress;
	
	@SuppressWarnings("unused")
	private GetSubmitProgressResult() {
		//Serialization only
	}

	public GetSubmitProgressResult(IOperationProgress progress) {
		super();
		this.progress = progress;
	}

	public IOperationProgress getProgress() {
		return progress;
	}
	
}
