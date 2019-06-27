package org.gcube.usecases.ws.thredds.model;

import org.gcube.usecases.ws.thredds.engine.impl.Process;

public interface CompletionCallback {

	public void onProcessCompleted(Process completedProcess);
	
}
