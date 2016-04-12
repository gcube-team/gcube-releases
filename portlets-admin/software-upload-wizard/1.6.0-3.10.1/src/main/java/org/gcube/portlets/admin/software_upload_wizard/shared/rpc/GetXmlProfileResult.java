package org.gcube.portlets.admin.software_upload_wizard.shared.rpc;

import net.customware.gwt.dispatch.shared.Result;

public class GetXmlProfileResult implements Result {
	
	private String xmlDocument;
	
	@SuppressWarnings("unused")
	private GetXmlProfileResult() {
		// Serialization only
	}

	public GetXmlProfileResult(String xmlDocument) {
		super();
		this.xmlDocument = xmlDocument;
	}
	
	public String getXmlProfile() {
		return xmlDocument;
	}

}
