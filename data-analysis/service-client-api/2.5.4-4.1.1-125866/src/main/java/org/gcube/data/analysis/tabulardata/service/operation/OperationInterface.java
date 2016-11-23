package org.gcube.data.analysis.tabulardata.service.operation;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.ExecutionFailedException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStepId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;

public interface OperationInterface {
	
	public List<OperationDefinition> getCapabilities();
	
	public OperationDefinition getCapability(long operationId) throws NoSuchOperationException;

	public Task execute(OperationExecution invocation, TabularResourceId tabularResourceId) throws NoSuchTabularResourceException,  NoSuchOperationException;
	
	public Task executeBatch(List<OperationExecution> invocations, TabularResourceId tabularResourceId) throws NoSuchTabularResourceException, NoSuchOperationException;
	
	public Task rollbackTo(TabularResourceId tabularResourceId, HistoryStepId historyStepId) throws NoSuchTabularResourceException, HistoryNotFoundException;
	 
	public List<Task> getTasks(TabularResourceId tabulaResourceId) throws NoSuchTabularResourceException;
	
	public List<Task> getTasks(TabularResourceId tabulaResourceId, TaskStatus status) throws NoSuchTabularResourceException;
	
	public Task getTask(TaskId taskId) throws NoSuchTaskException;
	
	public Task resubmit(TaskId taskId) throws NoSuchTaskException;
	
	public Task resume(TaskId taskId, Map<String, Object> operationInvocationParameter) throws NoSuchTaskException;

	public Task resume(TaskId taskId) throws NoSuchTaskException;
	
	public void executeSynchMetadataOperation(OperationExecution invocation, TabularResourceId tabularResourceId) throws NoSuchTabularResourceException,  NoSuchOperationException, ExecutionFailedException;
	
	public Task removeValidations(TabularResourceId tabularResourceId) throws NoSuchTabularResourceException;
}
