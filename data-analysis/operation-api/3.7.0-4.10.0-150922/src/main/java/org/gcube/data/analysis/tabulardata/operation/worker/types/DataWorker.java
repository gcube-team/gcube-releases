package org.gcube.data.analysis.tabulardata.operation.worker.types;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.results.WorkerResult;

public abstract class DataWorker extends Worker<WorkerResult> {

	public DataWorker(OperationInvocation sourceInvocation) {
		super(sourceInvocation);
	}

	
}
