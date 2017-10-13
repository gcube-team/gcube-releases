package org.gcube.data.analysis.tabulardata.service.operation;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.WorkerStatus;

public interface ValidationJob {

	float getProgress();
	
	String getHumaReadableStatus();
	
	WorkerStatus getStatus();
			
	Throwable getErrorMessage();
	
	String getDescription();
	
}
