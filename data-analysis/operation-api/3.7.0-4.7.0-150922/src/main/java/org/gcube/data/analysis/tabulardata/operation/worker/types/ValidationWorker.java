package org.gcube.data.analysis.tabulardata.operation.worker.types;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ValidityResult;

public abstract class ValidationWorker extends Worker<ValidityResult> {
		
	public ValidationWorker(OperationInvocation sourceInvocation) {
		super(sourceInvocation);
	}
	
}
