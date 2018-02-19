package org.gcube.data.analysis.tabulardata.service.impl.operation;

import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.operation;
import static org.gcube.data.analysis.tabulardata.clientlibrary.plugin.AbstractPlugin.tasks;

import java.util.List;
import java.util.Map;

import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.OperationManagerProxy;
import org.gcube.data.analysis.tabulardata.clientlibrary.proxy.TaskManagerProxy;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.ExecutionFailedException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchOperationException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTaskException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchOption;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TaskStatus;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.service.impl.operation.tasks.TaskFactory;
import org.gcube.data.analysis.tabulardata.service.operation.OperationInterface;
import org.gcube.data.analysis.tabulardata.service.operation.Task;
import org.gcube.data.analysis.tabulardata.service.operation.TaskId;
import org.gcube.data.analysis.tabulardata.service.tabular.HistoryStepId;
import org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId;

public class OperationInterfaceImpl implements OperationInterface {

	private static OperationManagerProxy operationManager = operation().build();

	private static TaskManagerProxy taskManager = tasks().build();

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#getCapabilities()
	 */
	public List<OperationDefinition> getCapabilities() {
		return operationManager.getCapabilities();
	}


	public OperationDefinition getCapability(long operationId)
			throws NoSuchOperationException {
		try{
			return operationManager.getCapabilities(operationId);
		}catch (OperationNotFoundException e) {
			throw new NoSuchOperationException(0);
		}
	}



	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#execute(org.gcube.data.td.commons.webservice.types.operations.OperationExecution, org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public Task execute(OperationExecution invocation,
			TabularResourceId tabularResourceId)
					throws NoSuchTabularResourceException, NoSuchOperationException{
		try{
			TaskInfo taskInfo = operationManager.execute(tabularResourceId.getValue(), invocation);
			return TaskFactory.getFactory().createTask(taskInfo);
		}catch (OperationNotFoundException e) {
			throw new NoSuchOperationException(0);
		}
	}

	@Override
	public void executeSynchMetadataOperation(OperationExecution invocation,
			TabularResourceId tabularResourceId)
					throws NoSuchTabularResourceException, NoSuchOperationException,
					ExecutionFailedException {
		try {
			operationManager.executeSynchMetadataOperation(tabularResourceId.getValue(), invocation);
		}catch (OperationNotFoundException e) {
			throw new NoSuchOperationException(0);
		}

	}


	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#executeBatch(java.util.List, org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	//TODO insert batch option
	public Task executeBatch(List<OperationExecution> invocations,
			TabularResourceId tabularResourceId)
					throws NoSuchTabularResourceException, NoSuchOperationException{
		try {
			TaskInfo taskInfo = operationManager.execute(tabularResourceId.getValue(), invocations, BatchOption.NONE);
			return TaskFactory.getFactory().createTask(taskInfo);
		}catch (OperationNotFoundException e) {
			throw new NoSuchOperationException(0);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#rollbackToTable(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId, org.gcube.data.analysis.tabulardata.model.table.TableId)
	 */
	public Task rollbackTo(TabularResourceId tabularResourceId,
			HistoryStepId historyStepId) throws NoSuchTabularResourceException,
			HistoryNotFoundException {
		TaskInfo taskInfo = operationManager.rollbackTo(tabularResourceId.getValue(), historyStepId.getValue());
		return TaskFactory.getFactory().createTask(taskInfo);
	}



	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#getTasks(org.gcube.data.analysis.tabulardata.service.tabular.TabularResourceId)
	 */
	public List<Task> getTasks(TabularResourceId tabularResourceId)
			throws NoSuchTabularResourceException {
		return TaskFactory.getFactory().getTasks(tabularResourceId, null);
	}



	/* (non-Javadoc)
	 * @see org.gcube.data.analysis.tabulardata.service.operation.OperationInterface#getTask(org.gcube.data.analysis.tabulardata.service.operation.TaskId)
	 */
	public Task getTask(TaskId taskId) throws NoSuchTaskException {
		return TaskFactory.getFactory().getTask(taskId);
	}


	@Override
	public List<Task> getTasks(TabularResourceId tabularResourceId,
			TaskStatus status) throws NoSuchTabularResourceException {
		return TaskFactory.getFactory().getTasks(tabularResourceId, status);
	}


	@Override
	public Task resubmit(TaskId taskId) throws NoSuchTaskException {
		TaskInfo taskInfo = taskManager.resubmit(taskId.getValue());
		return TaskFactory.getFactory().createTask(taskInfo);
	}


	@Override
	public Task resume(TaskId taskId, Map<String, Object> operationInvocationParameter) throws NoSuchTaskException {
		TaskInfo taskInfo = taskManager.resume(taskId.getValue(), operationInvocationParameter);
		return TaskFactory.getFactory().createTask(taskInfo);

	}

	@Override
	public Task resume(TaskId taskId) throws NoSuchTaskException {
		TaskInfo taskInfo = taskManager.resume(taskId.getValue());
		return TaskFactory.getFactory().createTask(taskInfo);

	}


	@Override
	public Task removeValidations(TabularResourceId tabularResourceId)
			throws NoSuchTabularResourceException {
		TaskInfo taskInfo = operationManager.removeValidations(tabularResourceId.getValue());
		return TaskFactory.getFactory().createTask(taskInfo);
	}

}
