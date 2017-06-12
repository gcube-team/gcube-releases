package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.factories.scopes.TableScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

public abstract class TableResourceCreatorWorkerFactory extends TableScopedWorkerFactory<ResourceCreatorWorker>   {

	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}
		
}
