package org.gcube.data.analysis.tabulardata.operation.worker.types;

import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.Worker;
import org.gcube.data.analysis.tabulardata.operation.worker.results.ResourcesResult;

public abstract class ResourceCreatorWorker extends Worker<ResourcesResult> {

	public ResourceCreatorWorker(OperationInvocation sourceInvocation) {
		super(sourceInvocation);
	}
	
}
