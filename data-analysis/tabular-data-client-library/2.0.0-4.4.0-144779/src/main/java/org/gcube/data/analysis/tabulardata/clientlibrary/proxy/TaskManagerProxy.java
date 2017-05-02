package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;

public interface TaskManagerProxy{

	TaskInfo remove(String identifier) throws NoSuchTaskException;
	
	List<TaskInfo> get(String ... identifiers);
	
	TaskInfo abort(String identifier) throws NoSuchTaskException;

	List<TaskInfo> getTasksByTabularResource(long tabularResourceId) throws NoSuchTabularResourceException;
	
	List<TaskInfo> getTasksByTabularResource(long tabularResourceId, TaskStatus status) throws NoSuchTabularResourceException;
	
	TaskInfo resubmit(String identifier) throws NoSuchTaskException;
	
	TaskInfo resume(final String identifier) throws NoSuchTaskException; 
	
	TaskInfo resume(final String identifier, Map<String, Object> operationInvocationParameter) throws NoSuchTaskException;
	
}
