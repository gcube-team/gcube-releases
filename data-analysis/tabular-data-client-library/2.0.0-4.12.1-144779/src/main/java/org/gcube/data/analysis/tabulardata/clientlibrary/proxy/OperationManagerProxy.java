package org.gcube.data.analysis.tabulardata.clientlibrary.proxy;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.exception.ExecutionFailedException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.HistoryNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.NoSuchTabularResourceException;
import org.gcube.data.analysis.tabulardata.commons.webservice.exception.OperationNotFoundException;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.BatchOption;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationDefinition;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;

public interface OperationManagerProxy {

	List<OperationDefinition> getCapabilities();
		
	OperationDefinition getCapabilities(long operationId) throws OperationNotFoundException;
	
	TaskInfo execute(long targetTabularResourceId, OperationExecution invocation)	throws NoSuchTabularResourceException,	OperationNotFoundException;

	TaskInfo execute(long targetTabularResourceId, List<OperationExecution> invocations, BatchOption option)	throws NoSuchTabularResourceException, 	OperationNotFoundException;

	TaskInfo rollbackTo(long tabularResourceId, long historyStepId)	throws NoSuchTabularResourceException, 	HistoryNotFoundException;

	public void executeSynchMetadataOperation(long targetTabularResourceId, OperationExecution invocation) throws NoSuchTabularResourceException, OperationNotFoundException, ExecutionFailedException;

	TaskInfo removeValidations(long tabularResourceId)
			throws NoSuchTabularResourceException;
}