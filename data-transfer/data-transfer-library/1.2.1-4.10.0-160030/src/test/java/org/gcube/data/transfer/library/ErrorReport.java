package org.gcube.data.transfer.library;

import org.gcube.data.transfer.model.TransferCapabilities;

public class ErrorReport extends TransferReport {

	String id;
	
	
	public ErrorReport(String id) {
		super(null);
		this.id=id;
	}

	@Override
	public String print() {
		return "ERROR : "+id;
	}
}
