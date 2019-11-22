package org.gcube.data.analysis.tabulardata.task.executor.operation.creators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.expression.Expression;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.Result;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.ResourceDescriptorResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.gcube.data.analysis.tabulardata.task.executor.ExecutionHolder;
import org.gcube.data.analysis.tabulardata.task.executor.ExecutionHolder.ResourceHolder;
import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationContext;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;

@Slf4j
public class OperationWorkerCreator extends WorkerCreator{

	@Override
	protected Worker<?> create(InternalInvocation invocation,
			OperationContext operationContext, ExecutionHolder executionHolder)
					throws InvalidInvocationException {
		OperationInvocation operationInvocation = createInvocation(invocation.getWorkerFactory(), invocation, operationContext.getCurrentTableId(), operationContext.getReferredTableId(), executionHolder);
		logger.trace("creating worker "+invocation.getWorkerFactory().getOperationDescriptor().getName()+" with parameters "+operationInvocation.getParameterInstances().toString());
		return invocation.getWorkerFactory().createWorker(operationInvocation);
	}

	@Override
	protected List<ValidationWorker> discoveryPreconditions(
			InternalInvocation invocation, OperationContext operationContext, ExecutionHolder executionHolder)
					throws Exception {

		List<ValidationWorker> preconditionWorkers = new ArrayList<>();

		for (Entry<String, WorkerFactory<ValidationWorker>> entry : invocation.getWorkerFactory().getPreconditionValidationMap().entrySet()){

			invocation.setParameters(invocation.getWorkerFactory().getParametersForPrecondion(entry.getKey(), operationContext.getCurrentTableId(),invocation.getColumnId(), invocation.getParameters()));

			OperationInvocation operationInvocation = createInvocation(entry.getValue(), invocation, operationContext.getCurrentTableId(), operationContext.getReferredTableId(), executionHolder);

			preconditionWorkers.add(entry.getValue().createWorker(operationInvocation));
		}
		return preconditionWorkers;
	}

	@Override
	public TableId resultCollector(ExecutionHolder executionHolder, Result result, OperationContext operationContext, OperationInvocation sourceInvocation) {

		if (result instanceof ResourcesResult){
			ResourcesResult resourcesResult = (ResourcesResult) result;
			for (ResourceDescriptorResult resourceResult : resourcesResult.getResources())
				executionHolder.addCreatedResource(new ResourceHolder(resourceResult
						, operationContext.getWorkerFactory().getOperationDescriptor().getOperationId().getValue()));
		} else if (result instanceof WorkerResult){
			WorkerResult workerResult = (WorkerResult) result;
			executionHolder.createStep(operationContext.getWorkerFactory(), sourceInvocation, workerResult, operationContext.getCurrentTableId());
			if (operationContext.getWorkerFactory().isRollbackable())
				executionHolder.removeOnFinish(operationContext.getCurrentTableId());	
			return workerResult.getResultTable().getId();
		}

		return operationContext.getCurrentTableId();
	}

	protected OperationInvocation createInvocation(WorkerFactory<?> factory, InternalInvocation invocation, TableId currentTableId, TableId referredTableId, ExecutionHolder executionHolder) throws InvalidInvocationException {
		InvocationCreator invocationCreator = InvocationCreator.getCreator(factory.getOperationDescriptor());
		if (referredTableId!=null)
			invocationCreator.setToUpdateReferredTable(referredTableId);
		if (invocation.getParameters()!=null){
			Map<String, Object> parameters = invocation.getParameters();
			if (executionHolder.areNewColumnsBeenCreated())
				parameters = updateParameters(parameters, executionHolder, referredTableId);
			invocationCreator.setParameters(parameters);
		}
		if (currentTableId != null)
			invocationCreator.setTargetTable(currentTableId);
		if (invocation.getColumnId()!=null){
			log.debug("CHECKING for column correspondance "+invocation.getColumnId());
			if (executionHolder.getColumnCorrespondance(invocation.getColumnId().getValue())!=null)
				invocationCreator.setTargetColumn(executionHolder.getColumnCorrespondance(invocation.getColumnId().getValue()));
			else invocationCreator.setTargetColumn(invocation.getColumnId());
		}
		return invocationCreator.create();
	}
		
	private Map<String, Object> updateParameters(
			Map<String, Object> parameters, ExecutionHolder executionHolder, TableId referredTableId) {
		Map<String, Object> toReturn = new HashMap<>();
		for (Entry<String, Object> entry : parameters.entrySet())
			toReturn.put(entry.getKey(), updateSingleParameter(entry.getValue(), executionHolder, referredTableId));
		return toReturn;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object updateSingleParameter(Object object, ExecutionHolder executionHolder, TableId referredTableId ){
		if (object instanceof ColumnReference){
			ColumnReference colRef = (ColumnReference) object;
			ColumnLocalId newId = executionHolder.getColumnCorrespondance(colRef.getColumnId().getValue());
			if (newId!=null && colRef.getTableId().equals(referredTableId))
				return new ColumnReference(referredTableId,newId, colRef.getType());
			else return colRef;
		}else if (object instanceof Expression){
			for (Expression subExpression: ((Expression) object).getLeavesByType(ColumnReference.class)){
				ColumnReference reference  = (ColumnReference)subExpression;
				ColumnLocalId newId = executionHolder.getColumnCorrespondance(reference.getColumnId().getValue());
				if (newId!=null && reference.getTableId().equals(referredTableId))
					((ColumnReference)subExpression).setColumnId(newId);
			}
			return object;
		} else if (object instanceof Map){
			return updateParameters((Map<String, Object>) object, executionHolder, referredTableId);
		}else if (object instanceof List){
			List<Object> toReturn = new ArrayList<>();
			for (Object obj : (List)object)
				toReturn.add(updateSingleParameter(obj, executionHolder, referredTableId));
			return toReturn;
		}else return object;
	}
		
}
