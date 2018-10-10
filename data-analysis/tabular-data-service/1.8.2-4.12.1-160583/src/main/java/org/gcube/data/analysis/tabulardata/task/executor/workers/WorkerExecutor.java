package org.gcube.data.analysis.tabulardata.task.executor.workers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.ValidationDescriptor;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerStatus;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.OperationAbortedException;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.Result;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ValidationWorker;
import org.gcube.data.analysis.tabulardata.task.executor.operation.OperationContext;
import org.gcube.data.analysis.tabulardata.task.executor.operation.PreconditionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerExecutor {

	private static Logger logger = LoggerFactory.getLogger(WorkerExecutor.class);
	
	private Worker<?> actualWorker =null;
	
	private boolean aborted = false;
	
	protected OperationContext operationContext;

	public WorkerExecutor(OperationContext operationContext) {
		this.operationContext = operationContext;
	}


	public Result executeOperation(Worker<?> worker) throws WorkerException, OperationAbortedException{
		Result result;
		
		this.actualWorker = worker;
		
		worker.run();
		
		if (aborted)
			throw new OperationAbortedException();
		
		if (worker.getStatus()==WorkerStatus.FAILED)
			throw worker.getException();

		result = worker.getResult();

		return result;
	}


	public void executeValidations(List<ValidationWorker> preconditionsWorker) throws WorkerException, OperationAbortedException{
		boolean valid = true;
		
		List<ValidationDescriptor> validationDescriptors = new ArrayList<>();
		
		Iterator<ValidationWorker> validationWorkerIt = preconditionsWorker.iterator();
		
		while (validationWorkerIt.hasNext() && !aborted){
			ValidationWorker validationWorker = validationWorkerIt.next();
			
			this.actualWorker = validationWorker;
			validationWorker.run();
			
			if (validationWorker.getStatus()==WorkerStatus.FAILED)
				throw validationWorker.getException();
			if (validationWorker.getStatus()==WorkerStatus.ABORTED)
				throw new OperationAbortedException();
			
			logger.trace("is precondition "+validationWorker.getSourceInvocation().getOperationDescriptor().getName()+" valid? "+validationWorker.getResult().isValid());	
			
			validationDescriptors.addAll(getValidationDescriptions(validationWorker.getResult()));
			valid &= validationWorker.getResult().isValid();
		}
		
		if (aborted)
			throw new OperationAbortedException();
		
		
		logger.trace("are precondition valid? "+valid);		
		
		operationContext.setPreconditionResult(new PreconditionResult(valid, validationDescriptors));
	}

	public static List<ValidationDescriptor> getValidationDescriptions(ValidityResult result){

		List<ValidationDescriptor> validations = new ArrayList<ValidationDescriptor>();
		
		for (org.gcube.data.analysis.tabulardata.operation.worker.results.ValidationDescriptor vd: result.getValidationDescriptors()){
			ValidationDescriptor descriptor = new ValidationDescriptor(vd.getDescription(), vd.isValid(), vd.getConditionCode());
			
			if (vd.getValidationColumn()!=null) descriptor.setValidationColumn(vd.getValidationColumn().getValue());
			
			validations.add(descriptor);
		}
				
		return validations;
	}

	public void abort(){
		if (actualWorker!=null)
			actualWorker.abort();
		this.aborted = true;
	}
	
}
