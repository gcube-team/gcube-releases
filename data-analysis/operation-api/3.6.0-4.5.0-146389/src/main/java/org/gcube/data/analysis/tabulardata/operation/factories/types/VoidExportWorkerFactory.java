package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.OperationType;
import org.gcube.data.analysis.tabulardata.operation.factories.scopes.VoidScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

public abstract class VoidExportWorkerFactory extends VoidScopedWorkerFactory<ResourceCreatorWorker> {
	
	@Override
	protected OperationType getOperationType() {
	 return OperationType.EXPORT;
	}
	
	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}
	
}
