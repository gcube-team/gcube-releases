package org.gcube.portlets.admin.software_upload_wizard.client.rpc;

import net.customware.gwt.dispatch.client.ExceptionHandler;

import com.allen_sauer.gwt.log.client.Log;

public class LoggingExceptionHandler implements ExceptionHandler {

	@Override
	public Status onFailure(Throwable e) {
		Log.error("RPC Error",e);
//		MessageBox.alert("RPC Error", e.getMessage(),null);
//		Info.display("RPC Error", e.getMessage());
		return Status.CONTINUE;
	}

}
