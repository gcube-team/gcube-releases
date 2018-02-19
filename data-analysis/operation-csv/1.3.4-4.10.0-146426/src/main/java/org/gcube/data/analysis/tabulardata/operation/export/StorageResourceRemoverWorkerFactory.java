package org.gcube.data.analysis.tabulardata.operation.export;

import org.gcube.data.analysis.tabulardata.operation.factories.types.ExportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemover;
import org.gcube.data.analysis.tabulardata.operation.worker.results.resources.remover.ResourceRemoverProvider;

public abstract class StorageResourceRemoverWorkerFactory extends
		ExportWorkerFactory implements ResourceRemoverProvider{

	StorageRemover remover = new StorageRemover();
	
	@Override
	public ResourceRemover getResourceRemover() {
		return remover;
	}

}
