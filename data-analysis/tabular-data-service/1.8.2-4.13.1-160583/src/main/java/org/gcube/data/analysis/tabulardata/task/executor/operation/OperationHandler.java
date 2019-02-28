package org.gcube.data.analysis.tabulardata.task.executor.operation;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.Result;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ColumnCreatorWorker;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.gcube.data.analysis.tabulardata.task.executor.ExecutionHolder;
import org.gcube.data.analysis.tabulardata.task.executor.operation.creators.WorkerCreator;
import org.gcube.data.analysis.tabulardata.task.executor.operation.listener.ExecutionListener;
import org.gcube.data.analysis.tabulardata.task.executor.workers.WorkerExecutor;
import org.gcube.data.analysis.tabulardata.utils.InternalInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class OperationHandler {

	private static Logger logger = LoggerFactory.getLogger(OperationHandler.class);
	
	private WorkerExecutor workerExecutor;

	private OperationContext operationContext;

	private WorkerCreator workerCreator;

	private ExecutionListener listener;

	public OperationHandler(OperationContext operationContext,
			WorkerCreator workerCreator,ExecutionListener listener) {
		super();
		this.workerExecutor = new WorkerExecutor(operationContext);
		this.operationContext = operationContext;
		this.workerCreator = workerCreator;
		this.listener = listener;
	}



	public TableId run(ExecutionHolder executionHolder, InternalInvocation invocation) throws WorkerException, OperationAbortedException{

		List<ValidationWorker> preconditions;
		
		Worker<?> worker;

		try{
			worker = workerCreator.getWorker(invocation, operationContext, executionHolder);
		}catch(InvalidInvocationException iie){
			throw new WorkerException("error invoking worker : "+iie.getMessage(), iie);
		}
		
		try{
			preconditions = workerCreator.getPreconditions(invocation, operationContext, executionHolder);
		}catch(Exception iie){
			throw new WorkerException("error invoking preconditions : "+iie.getMessage(), iie);
		}
		if (!preconditions.isEmpty()){
			workerExecutor.executeValidations(preconditions);
		
			logger.trace("precondition result "+operationContext.getPreconditionResult());
			
			operationContext.getTaskStep().addValidations(operationContext.getPreconditionResult().getValidations());
						
			if (!operationContext.getPreconditionResult().isValid())
				if (!listener.onStop(operationContext)) return operationContext.getCurrentTableId();
		}
		
		Result result = workerExecutor.executeOperation(worker);
				
		if (worker instanceof ColumnCreatorWorker){
			//adding column created mapping
			List<ColumnLocalId> createdColumns = ((ColumnCreatorWorker) worker).getCreatedColumns();
			int index =0;
			for (ColumnLocalId columnId: createdColumns){
				String columnInvocationId =InternalInvocation.getDinamicallyCreatedColumnId(invocation.getInvocationId(),index++);
				log.debug("ADDING column "+columnInvocationId+" associated with "+columnId.getValue());
				executionHolder.addColumnCreatedMapping(columnInvocationId, columnId);
			}
		}else if (worker instanceof ValidationWorker){
			operationContext.getTaskStep().addValidations(WorkerExecutor.getValidationDescriptions((ValidityResult) result));
			logger.trace(worker.getClass().getSimpleName()+" has resulted valid? "+((ValidityResult) result).isValid());
			if (!((ValidityResult) result).isValid())
				if (!listener.onStop(operationContext)) return operationContext.getCurrentTableId();
		}
		return workerCreator.resultCollector(executionHolder, result, operationContext, worker.getSourceInvocation());

	}

	public WorkerExecutor getWorkerExecutor() {
		return workerExecutor;
	}

}
