package org.gcube.data.analysis.tabulardata.service.impl.operation.tasks;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;

public interface TaskObserver {
		
	public abstract void notify(TaskInfo task);
	
	public abstract String getObserverIdentifier();

}
