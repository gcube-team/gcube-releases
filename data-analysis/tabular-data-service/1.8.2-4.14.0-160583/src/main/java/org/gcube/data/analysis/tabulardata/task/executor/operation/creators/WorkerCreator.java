package org.gcube.data.analysis.tabulardata.task.executor.operation.creators;

import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationStep;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.Result;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.gcube.data.analysis.tabulardata.task.TaskStepUpdater;
import org.gcube.data.analysis.tabulardata.task.ValidationStepUpdater;
import org.gcube.data.analysis.tabulardata.task.executor.ExecutionHolder;
import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationContext;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WorkerCreator {

	protected static Logger logger = LoggerFactory.getLogger(WorkerCreator.class); 

	protected abstract Worker<?> create(InternalInvocation invocation, OperationContext operationContext, ExecutionHolder executionHolder) throws InvalidInvocationException;

	protected abstract List<ValidationWorker> discoveryPreconditions(InternalInvocation invocation, OperationContext operationContext, ExecutionHolder executionHolder) throws Exception;

	public abstract TableId resultCollector(ExecutionHolder executionHolder, Result result, OperationContext operationContext, OperationInvocation sourceInvocation);

	public final Worker<?> getWorker(InternalInvocation invocation, OperationContext operationContext,ExecutionHolder executionHolder) throws InvalidInvocationException{
		Worker<?> worker = this.create(invocation, operationContext, executionHolder);
		TaskStepUpdater updater =new TaskStepUpdater(operationContext.getTaskStep());
		worker.addObserver(updater);
		return worker;
	}

	public List<ValidationWorker> getPreconditions(InternalInvocation invocation, OperationContext operationContext, ExecutionHolder executionHolder) throws Exception{

		List<ValidationWorker> preconditions = discoveryPreconditions(invocation, operationContext, executionHolder);

		if (!preconditions.isEmpty()){

			List<ValidationStep> validationSteps = operationContext.getTaskStep().getValidationSteps();

			if (validationSteps.size()!=preconditions.size()) throw new Exception("error on preconditions");

			for (int i=0; i<preconditions.size(); i++)
				preconditions.get(i).addObserver((ValidationStepUpdater)validationSteps.get(i));
		}

		return preconditions;

	}

}
