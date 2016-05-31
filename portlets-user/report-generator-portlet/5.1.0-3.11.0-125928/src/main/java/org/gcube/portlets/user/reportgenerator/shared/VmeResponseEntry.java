package org.gcube.portlets.user.reportgenerator.shared;

import java.io.Serializable;

/**
 * Simply wraps the ResponseEntry class of the RSG WS for sending them to the client
 * @author Massimiliano Assante, ISTI-CNR
 *
 */
@SuppressWarnings("serial")
public class VmeResponseEntry implements Serializable {
	private String responseEntryCode;
	private String responseMessage;

	public VmeResponseEntry() {
		super();
	}

	public VmeResponseEntry(String responseEntryCode, String responseMessage) {
		super();
		this.responseEntryCode = responseEntryCode;
		this.responseMessage = responseMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseEntryCode() {
		return responseEntryCode;
	}

	public void setResponseEntryCode(String responseEntryCode) {
		this.responseEntryCode = responseEntryCode;
	}	
}
