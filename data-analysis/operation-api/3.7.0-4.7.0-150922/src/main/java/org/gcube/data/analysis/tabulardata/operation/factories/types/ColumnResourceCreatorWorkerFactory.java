package org.gcube.data.analysis.tabulardata.operation.factories.types;

import org.gcube.data.analysis.tabulardata.operation.factories.scopes.ColumnScopedWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemoverProvider;
import org.gcube.data.analysis.tabulardata.operation.worker.types.ResourceCreatorWorker;

public abstract class ColumnResourceCreatorWorkerFactory extends ColumnScopedWorkerFactory<ResourceCreatorWorker> implements ResourceRemoverProvider {

	@Override
	public Class<ResourceCreatorWorker> getWorkerType() {
		return ResourceCreatorWorker.class;
	}
	
}
