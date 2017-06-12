package org.gcube.data.analysis.tabulardata.operation.worker.types;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;

public abstract class MetadataWorker extends Worker<EmptyType> {

	public MetadataWorker(OperationInvocation sourceInvocation) {
		super(sourceInvocation);
	}
	
}
