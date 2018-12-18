package org.gcube.usecases.ws.thredds.model;

import org.gcube.usecases.ws.thredds.engine.impl.ProcessDescriptor;
import org.gcube.usecases.ws.thredds.engine.impl.ProcessStatus;

public interface SyncOperationCallBack {

	public void onStep(ProcessStatus status, ProcessDescriptor descriptor);
	
}
