package org.gcube.data.analysis.tabulardata.task.executor.operation.creators;

import java.util.Collections;
import java.util.List;

import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.Result;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.RollbackWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.gcube.data.analysis.tabulardata.task.executor.ExecutionHolder;
import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationContext;
import org.gcube.data.analysis.tabulardata.task.executor.workers.NopWorker;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;

public class RollbackWorkerCreator extends WorkerCreator{


	@Override
	protected Worker<?> create(InternalInvocation invocation,
			OperationContext operationContext, ExecutionHolder executionHolder) throws InvalidInvocationException{
		if (invocation.isNop())
			return new NopWorker(invocation.getDiffTable(), operationContext.getCurrentTable());

		OperationInvocation opInvocation = createInvocation(invocation, operationContext.getCurrentTableId(), operationContext.getReferredTableId());
		try{
			RollbackWorker worker = invocation.getWorkerFactory().createRollbackWoker(invocation.getDiffTable(), operationContext.getCurrentTable(), opInvocation);
			return worker;
		}catch(UnsupportedOperationException e){
			logger.warn("rollback is not supported for operation "+invocation.getWorkerFactory().getClass());
			throw new InvalidInvocationException(opInvocation, e);
		}
	}

	@Override
	protected List<ValidationWorker> discoveryPreconditions(InternalInvocation invocation,
			OperationContext operationContext, ExecutionHolder executionHolder) throws Exception{
		return Collections.emptyList();
	}

	protected OperationInvocation createInvocation(InternalInvocation invocation, TableId currentTableId, TableId referredTableId) throws InvalidInvocationException {
		InvocationCreator invocationCreator = InvocationCreator.getCreator(invocation.getWorkerFactory().getOperationDescriptor());
		if (invocation.getParameters()!=null)
			invocationCreator.setParameters(invocation.getParameters());
		if (currentTableId != null)
			invocationCreator.setTargetTable(currentTableId);
		if (invocation.getColumnId()!=null)
			invocationCreator.setTargetColumn(invocation.getColumnId());
		try{
			return invocationCreator.create();
		}catch(RuntimeException re){
			throw new InvalidInvocationException(null, re);
		}
	}

	@Override
	public TableId resultCollector(ExecutionHolder executionHolder, Result result, OperationContext operationContext, OperationInvocation sourceInvocation) {
		WorkerResult wResult = (WorkerResult) result;
		logger.trace("adding to remove on finish "+operationContext.getCurrentTableId());
		executionHolder.removeOnFinish(operationContext.getCurrentTableId());
		return wResult.getResultTable().getId();
	}

}
