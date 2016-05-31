package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import java.util.ArrayList;

import org.gcube.portlets.admin.software_upload_wizard.shared.Deliverable;

import net.customware.gwt.dispatch.shared.Result;

public class GetDeliverablesResult implements Result {

private ArrayList<Deliverable> deliverables;
	
	@SuppressWarnings("unused")
	private GetDeliverablesResult() {
		// Serialization only
	}

	public GetDeliverablesResult(ArrayList<Deliverable> files) {
		super();
		this.deliverables = files;
	}
	
	public ArrayList<Deliverable> getDeliverables() {
		return deliverables;
	}
}
