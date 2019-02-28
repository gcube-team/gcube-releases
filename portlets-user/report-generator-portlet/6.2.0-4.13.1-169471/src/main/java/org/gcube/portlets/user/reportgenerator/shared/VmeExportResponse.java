package org.gcube.portlets.user.reportgenerator.shared;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Simply wraps the Response clases of the RSG WS for sending them to the client
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class VmeExportResponse implements Serializable {
	private boolean globallySucceded;
	
	private ArrayList<VmeResponseEntry> responseMessageList;

	public VmeExportResponse() {		
		super();
		responseMessageList = new ArrayList<VmeResponseEntry>();
	}
	
	public VmeExportResponse(VmeResponseEntry singleResponse) {
		responseMessageList = new ArrayList<VmeResponseEntry>();
		responseMessageList.add(singleResponse);
	}

	public VmeExportResponse(ArrayList<VmeResponseEntry> responseMessageList) {
		super();
		this.responseMessageList = responseMessageList;
	}

	public ArrayList<VmeResponseEntry> getResponseMessageList() {
		if (responseMessageList == null)
			responseMessageList = new ArrayList<VmeResponseEntry>();
		return responseMessageList;
	}

	public void setResponseMessageList(ArrayList<VmeResponseEntry> responseMessageList) {
		this.responseMessageList = responseMessageList;
	}

	public boolean isGloballySucceded() {
		return globallySucceded;
	}

	public void setGloballySucceded(boolean globallySucceded) {
		this.globallySucceded = globallySucceded;
	}
	
}
